package com.gw.gwmall.service.impl;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.common.vo.ESProductUpdateParam;
import com.gw.gwmall.component.rocketmq.CanalRocketMqSender;
import com.gw.gwmall.domain.EsProductResponse;
import com.gw.gwmall.promotion.SearchFeignApi;
import com.gw.gwmall.service.IProcessCanalData;
import com.gw.gwmall.util.CanalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

/**
 * 异构数据库下es的实时数据同步，
 * 商品数据的变化从MySQL同步到ElasticSearch
 */
@Service
@Slf4j
public class ProductESData implements IProcessCanalData {

    @Autowired
    @Qualifier("productConnector")
    private CanalConnector connector;

    @Value("${canal.product.subscribe:server}")
    private String subscribe;

    @Value("${canal.product.batchSize}")
    private int batchSize;

    @Autowired
    private SearchFeignApi searchFeignApi;

    @Autowired
    private CanalRocketMqSender canalRocketMqSender;

    @PostConstruct
    @Override
    public void connect() {
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
    @Scheduled(initialDelayString = "${canal.product.initialDelay:5000}", fixedDelayString = "${canal.product.fixedDelay:1000}")
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
                return;
            }
            List<ESProductUpdateParam> updateList = new ArrayList<>();
            for (CanalEntry.Entry entry : message.getEntries()) {
                if (CanalUtil.isTransactionalLog(entry)) {
                    continue;
                }
                processEntry(entry, updateList);
                if (updateList.size() > 100) {
                    batchProcessUpdateList(updateList);
                    updateList.clear();
                }
            }
            if (updateList.size() > 0) {
                batchProcessUpdateList(updateList);
            }
            connector.ack(batchId); // 提交确认
        } catch (Exception e) {
            log.error("处理商品Canal同步数据失效，请检查：", e);
        }
    }

    private void processEntry(CanalEntry.Entry entry, final List<ESProductUpdateParam> updateList)
            throws InvalidProtocolBufferException {
        CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
        String tableName = entry.getHeader().getTableName();
        CanalEntry.EventType eventType = rowChange.getEventType();
        log.info("数据变更详情：来自binglog[{}.{}]，数据源{}.{}，变更类型{}",
                entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                entry.getHeader().getSchemaName(), tableName, eventType);
        for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
            if (eventType == CanalEntry.EventType.DELETE) {
                List<CanalEntry.Column> columns = rowData.getBeforeColumnsList();
                for (CanalEntry.Column column : columns) {
                    if (column.getName().equals("id")) {
                        updateList.add(new ESProductUpdateParam(Long.valueOf(column.getValue()),
                                ESProductUpdateParam.CHANGE_TYPE_DELETE));
                        break;
                    }
                }
            } else if (eventType == CanalEntry.EventType.INSERT ||
                    eventType == CanalEntry.EventType.UPDATE) {
                int opType = ESProductUpdateParam.CHANGE_TYPE_ADD;
                if (eventType == CanalEntry.EventType.UPDATE) {
                    opType = ESProductUpdateParam.CHANGE_TYPE_UPDATE;
                }
                final int type = opType;
                List<CanalEntry.Column> columns = rowData.getAfterColumnsList();
                columns.stream()
                        .filter(e -> "id".equals(e.getName()))
                        .findFirst()
                        .map(CanalEntry.Column::getValue)
                        .ifPresent(id -> updateList.add(new ESProductUpdateParam(Long.valueOf(id),
                                type)));
            }
        }
    }

    private void batchProcessUpdateList(List<ESProductUpdateParam> updateList) {
        log.info("变更值为:[{}]", updateList);
        try {
            this.canalRocketMqSender.sendProductChangeMessage(updateList);
//            CommonResult<List<EsProductResponse>> listCommonResult = this.searchFeignApi.batchChangeProductList(updateList);
//            log.info("处理结果:[{}]", listCommonResult.getData());
        } catch (Exception ex) {
            log.info("上传失败:", ex);
            //上传失败后，使用mq作为备案
        }
    }
}
