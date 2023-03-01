package com.gw.gwmall.history.service.impl;

import com.gw.gwmall.history.domain.OmsOrderDetail;
import com.gw.gwmall.history.service.MigrateCentreService;
import com.gw.gwmall.history.service.OperateDbService;
import com.gw.gwmall.history.service.OperateMgDbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class MigrateCentreServiceImpl implements MigrateCentreService {

    private static final int TABLE_NO = 31;
    private static final int FETCH_RECORD_NUMBERS = 2000;
    private static final int DB_SLEEP_RND = 5;

    private static BlockingQueue<Runnable> tableQueue = new ArrayBlockingQueue<>(TABLE_NO + 1);

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(0, Runtime.getRuntime().availableProcessors(),
            10, TimeUnit.SECONDS, tableQueue);

    private AtomicBoolean roundOnOff = new AtomicBoolean(true);

    @Autowired
    private OperateMgDbService operateMgDbService;
    @Autowired
    private OperateDbService operateDbService;

    public void stop() {
        roundOnOff.set(false);
        tableQueue.clear();
    }

    @Override
    public void recover() {
        roundOnOff.set(true);
    }

    public String migrateAllOrderTables() {
        try {
            this.recover();
            for (int i = 0; i <= TABLE_NO; i++) {
                final int tableCount = i;
                executor.execute(() -> migrateSingleOrderTable(tableCount));
            }
            return OrderConstant.MIGRATE_SUCCESS;
        } catch (Exception e) {
            log.error("执行任务失败：", e);
            return OrderConstant.MIGRATE_FAILURE;
        }

    }

    /*完成单表从MySQL到MongoDB的一轮(Round)数据迁移，依然需要分次进行，
    每次的数据条数由FETCH_RECORD_NUMBERS控制，
    该控制阈值可以写入配置中心动态调整，建议不要超过10000*/
    @Override
    @Transactional("dbTransactionManager")
    public String migrateSingleOrderTable(int tableNo) {
        try {
            this.recover();
            /*获得3个月前的时间*/
            Calendar calendar = Calendar.getInstance();
            //calendar.add(Calendar.MONTH, -3);
            calendar.add(Calendar.DATE, 0);
            Date maxDate = calendar.getTime();
            String factTableName = OrderConstant.OMS_ORDER_NAME_PREFIX + tableNo;
            /*本轮处理数据的最小ID和最大ID*/
            long roundMinOrderId = operateMgDbService.getMaxOrderId(factTableName);
            long roundMaxOrderId = roundMinOrderId;
            log.info("本轮表[{}]数据迁移查询记录起始ID = {}", factTableName, roundMinOrderId);
            /*开始迁移*/
            while (roundOnOff.get()) {
                /*获得上次处理的最大OrderId，作为本次迁移的起始ID*/
                long currMaxOrderId = operateMgDbService.getMaxOrderId(factTableName);
                log.info("本次表[{}]数据迁移查询记录起始ID = {}", factTableName, currMaxOrderId);
                List<OmsOrderDetail> fetchRecords = operateDbService.getOrders(currMaxOrderId,
                        tableNo, maxDate, FETCH_RECORD_NUMBERS);
                if (CollectionUtils.isEmpty(fetchRecords)) {
                    log.info("表[{}]本轮数据迁移已完成，数据截止时间={}，min={},max={}",
                            factTableName, maxDate, roundMinOrderId, roundMaxOrderId);
                    break;
                }
                int fetchSize = fetchRecords.size();
                /*更新最大OrderId，记录本次迁移的最小ID*/
                currMaxOrderId = fetchRecords.get(fetchRecords.size() - 1).getId();
                long curMinOrderId = fetchRecords.get(0).getId();
                log.info("开始进行表[{}]数据迁移，应该迁移记录截止时间={},记录条数={}，min={},max={}",
                        factTableName, maxDate, fetchSize, curMinOrderId, currMaxOrderId);
                operateMgDbService.saveToMgDb(fetchRecords, currMaxOrderId, factTableName);
                /*更新本轮处理数据的最大ID*/
                roundMaxOrderId = currMaxOrderId;
                log.info("表[{}]本次数据迁移已完成，准备删除记录", factTableName);
                operateDbService.deleteOrders(tableNo, curMinOrderId, currMaxOrderId);
                /*考虑到数据库的负载，每次迁移后休眠随机数时间*/
                int rnd = ThreadLocalRandom.current().nextInt(DB_SLEEP_RND);
                log.info("表[{}]本次数据删除已完成，休眠[{}]S", factTableName, rnd);
                TimeUnit.SECONDS.sleep(rnd);

            }
            return OrderConstant.MIGRATE_SUCCESS;
        } catch (Exception e) {
            log.error("表[{}]本次数据迁移异常，已终止，请检查并手工修复：",
                    OrderConstant.OMS_ORDER_NAME_PREFIX + tableNo, e);
            return OrderConstant.MIGRATE_FAILURE;
        }
    }
}
