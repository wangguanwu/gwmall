package com.gw.gwmall.service.impl;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.gw.gwmall.common.util.JacksonUtils;
import com.gw.gwmall.config.PromotionRedisKey;
import com.gw.gwmall.rediscomm.util.RedisOpsExtUtil;
import com.gw.gwmall.service.IProcessCanalData;
import com.gw.gwmall.util.CanalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;

//@Service
@Slf4j
public class PromotionData implements IProcessCanalData {

    private final static String SMS_HOME_ADVERTISE = "sms_home_advertise";
    private final static String SMS_HOME_BRAND = "sms_home_brand";
    private final static String SMS_HOME_NEW_PRODUCT = "sms_home_new_product";
    private final static String SMS_HOME_RECOMMEND_PRODUCT = "sms_home_recommend_product";
    /*存储从表名到Redis缓存的键*/
    private Map<String, String> tableMapKey = new HashMap<>();

    @Autowired
    @Qualifier("promotionConnector")
    private CanalConnector connector;

    @Autowired
    private PromotionRedisKey promotionRedisKey;

    @Autowired
    private RedisOpsExtUtil redisOpsExtUtil;

    @Value("${canal.promotion.subscribe:server}")
    private String subscribe;

    @Value("${canal.promotion.batchSize}")
    private int batchSize;

    @PostConstruct
    @Override
    public void connect() {
        tableMapKey.put(SMS_HOME_ADVERTISE, promotionRedisKey.getHomeAdvertiseKey());
        tableMapKey.put(SMS_HOME_BRAND, promotionRedisKey.getBrandKey());
        tableMapKey.put(SMS_HOME_NEW_PRODUCT, promotionRedisKey.getNewProductKey());
        tableMapKey.put(SMS_HOME_RECOMMEND_PRODUCT, promotionRedisKey.getRecProductKey());
        connector.connect();
        if ("server".equals(subscribe))
            connector.subscribe(null);
        else
            connector.subscribe(subscribe);
        connector.rollback();
    }

    @PreDestroy
    @Override
    public void disConnect() {
        connector.disconnect();
    }

    @Async
    @Scheduled(initialDelayString = "${canal.promotion.initialDelay:5000}", fixedDelayString = "${canal.promotion.fixedDelay:5000}")
    @Override
    public void processData() {
        try {
            if (!connector.checkValid()) {
                log.warn("与Canal服务器的连接失效！！！重连，下个周期再检查数据变更");
                this.connect();
                return;
            }
            Message message = connector.getWithoutAck(batchSize);
            long batchId = message.getId();
            int size = message.getEntries().size();
            if (batchId == -1 || size == 0) {
//                log.info("本次[{}]没有检测到促销数据更新。", batchId);
                connector.ack(batchId); // 提交确认
                return;
            }

//            log.info("本次[{}]促销数据本次共有[{}]次更新需要处理", batchId, size);
            /*一个表在一次周期内可能会被修改多次，而对Redis缓存的处理只需要处理一次即可*/
            Set<String> factKeys = new HashSet<>();
            for (CanalEntry.Entry entry : message.getEntries()) {
                if (CanalUtil.isTransactionalLog(entry)) {
                    continue;
                }
                CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                String tableName = entry.getHeader().getTableName();
                CanalEntry.EventType eventType = rowChange.getEventType();
                log.info("数据变更详情：来自binglog[{}.{}]，数据源{}.{}，变更类型{}",
                        entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                        entry.getHeader().getSchemaName(), tableName, eventType);
                List<CanalEntry.RowData> rowDatasList = rowChange.getRowDatasList();
                Map<String, String> map = new HashMap<>();
                for (CanalEntry.RowData row : rowDatasList) {
                    if (eventType == CanalEntry.EventType.UPDATE) {
                        List<CanalEntry.Column> columns = row.getAfterColumnsList();
                        for (CanalEntry.Column column : columns) {
                            map.put(column.getName(), column.getValue());
                        }
                    } else if (eventType == CanalEntry.EventType.INSERT) {
                        List<CanalEntry.Column> columns = row.getAfterColumnsList();
                        for (CanalEntry.Column column : columns) {
                            map.put(column.getName(), column.getValue());
                        }
                    } else if (eventType == CanalEntry.EventType.DELETE) {
                        List<CanalEntry.Column> columns = row.getBeforeColumnsList();
                        for (CanalEntry.Column column : columns) {
                            map.put(column.getName(), column.getValue());
                        }
                    }
                    log.info("变更数据:{}",JacksonUtils.toJson(map));
                }
                factKeys.add(tableMapKey.get(tableName));
            }
            for (String key : factKeys) {
                if (StringUtils.isNotEmpty(key)) {
                    redisOpsExtUtil.delete(key);
                }
            }
            connector.ack(batchId); // 提交确认
            log.info("本次[{}]处理促销Canal同步数据完成", batchId);
        } catch (Exception e) {
            log.error("处理促销Canal同步数据失效，请检查：", e);
        }
    }
}
