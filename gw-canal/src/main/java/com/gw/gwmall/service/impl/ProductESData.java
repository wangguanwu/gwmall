package com.gw.gwmall.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.gw.gwmall.domain.ProductESVo;
import com.gw.gwmall.service.IProcessCanalData;
import com.gw.gwmall.util.CanalUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * 演示异构数据库下的实时数据同步，
 * 商品数据的变化从MySQL同步到ElasticSearch，本类未经测试，仅供参考
 * 同时商品数据的变化从业务上来说，也应该同步一份到Redis，本次课程略过具体实现
 */
//@Service
@Slf4j
public class ProductESData implements IProcessCanalData {

    private final static String T_ID = "id";
    private final static String T_NAME = "name";
    private final static String T_KEYWORDS = "keywords";
    private final static String T_SUB_TITLE = "sub_title";
    private final static String T_PRICE = "price";
    private final static String T_PROMOTION_PRICE = "promotion_price";
    private final static String T_ORIGINAL_PRICE = "original_price";
    private final static String T_PIC = "pic";
    private final static String T_SALE = "sale";
    private final static String T_BRAND_ID = "brand_id";
    private final static String T_BRAND_NAME = "brand_name";
    private final static String T_PRODUCT_CATEGORY_ID = "product_category_id";
    private final static String T_PRODUCT_CATEGORY_NAME = "product_category_name";

    @Value("${canal.product.indexName}")
    private String indexName;

    @Autowired
    @Qualifier("productConnector")
    private CanalConnector connector;

    @Value("${canal.product.subscribe:server}")
    private String subscribe;

    @Value("${canal.product.batchSize}")
    private int batchSize;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

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
                log.info("本次没有检测到商品数据更新。");
                return;
            }
            for (CanalEntry.Entry entry : message.getEntries()) {
                if (CanalUtil.isTransactionalLog(entry)) {
                    continue;
                }
                CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                String tableName = entry.getHeader().getTableName();
                CanalEntry.EventType eventType = rowChange.getEventType();
                log.debug("数据变更详情：来自binglog[{}.{}]，数据源{}.{}，变更类型{}",
                        entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                        entry.getHeader().getSchemaName(), tableName, eventType);
                for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                    if (eventType == CanalEntry.EventType.DELETE) {
                        List<CanalEntry.Column> columns = rowData.getBeforeColumnsList();
                        for (CanalEntry.Column column : columns) {
                            if (column.getName().equals("id")) {
                                deleteDoc(column.getValue());
                                break;
                            }
                        }
                    } else if (eventType == CanalEntry.EventType.INSERT) {
                        List<CanalEntry.Column> columns = rowData.getAfterColumnsList();
                        ProductESVo productESVo = new ProductESVo();
                        String docId = makeVo(columns, productESVo);
                        insertDoc(docId, productESVo);
                    } else {
                        List<CanalEntry.Column> columns = rowData.getAfterColumnsList();
                        ProductESVo productESVo = new ProductESVo();
                        String docId = makeVo(columns, productESVo);
                        if (null != docId) {
                            updateDoc(docId, productESVo);
                        }
                    }
                }
            }
            connector.ack(batchId); // 提交确认
        } catch (Exception e) {
            log.error("处理商品Canal同步数据失效，请检查：", e);
        }
    }

    private String makeVo(List<CanalEntry.Column> columns, ProductESVo productESVo) {
        String docId = null;
        for (CanalEntry.Column column : columns) {
            String colName = column.getName();
            String colValue = column.getValue();
            if (colName.equals(T_ID)) {
                docId = colValue;
            } else if (colName.equals(T_NAME)) {
                productESVo.setName(colValue);
            }
            if (colName.equals(T_KEYWORDS)) {
                productESVo.setKeywords(colValue);
            }
            if (colName.equals(T_SUB_TITLE)) {
                productESVo.setSubTitle(colValue);
            }
            if (colName.equals(T_PRICE)) {
                productESVo.setPrice(new BigDecimal(colValue));
            }
            if (colName.equals(T_PROMOTION_PRICE)) {
                productESVo.setPromotionPrice(new BigDecimal(colValue));
            }
            if (colName.equals(T_ORIGINAL_PRICE)) {
                productESVo.setOriginalPrice(new BigDecimal(colValue));
            }
            if (colName.equals(T_PIC)) {
                productESVo.setPic(colValue);
            }
            if (colName.equals(T_SALE)) {
                productESVo.setSaleCount(Integer.valueOf(colValue));
            }
            if (colName.equals(T_BRAND_ID)) {
                productESVo.setBrandId(Long.valueOf(colValue));
            }
            if (colName.equals(T_BRAND_NAME)) {
                productESVo.setBrandName(colValue);
            }
            if (colName.equals(T_PRODUCT_CATEGORY_ID)) {
                productESVo.setCategoryId(Long.valueOf(colValue));
            }
            if (colName.equals(T_PRODUCT_CATEGORY_NAME)) {
                productESVo.setCategoryName(colValue);
            }
            if (colName.equals("delete_status")) {
                if (1 == Integer.valueOf(colValue)) {
                    productESVo = null;
                }
            }
        }
        return docId;
    }

    private void deleteDoc(String docId) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(indexName, docId);
        DeleteResponse deleteResponse =
                restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        String indexDoc = indexName + "/" + docId;
        if (deleteResponse.getResult() == DocWriteResponse.Result.NOT_FOUND) {
            log.warn("删除不存在的文档 {}", indexDoc);
        } else {
            log.info("删除文档 {} 成功", indexDoc);
        }
    }

    private void updateDoc(String docId, ProductESVo productESVo) throws IOException {
        String productJson = JSONObject.toJSONString(productESVo);
        /*XContentBuilder xContentBuilder = XContentFactory.jsonBuilder();
        xContentBuilder.startObject();
        for (Map.Entry<String, String> entry : updateField.entrySet()) {
            xContentBuilder.field(entry.getKey(), entry.getValue());
        }
        xContentBuilder.endObject();*/
        UpdateRequest request =
                new UpdateRequest(indexName, docId).doc(productJson, XContentType.JSON);
        request.docAsUpsert(true);
        UpdateResponse updateResponse =
                restHighLevelClient.update(request, RequestOptions.DEFAULT);
        String indexDoc = indexName + "/" + docId;
        if (updateResponse.getResult() == DocWriteResponse.Result.CREATED) {
            log.info("文档 {} 不存在，更新变更为创建成功", indexDoc);
        } else if (updateResponse.getResult() == DocWriteResponse.Result.UPDATED) {
            log.info("文档 {} 不存在，更新成功", indexDoc);
        } else if (updateResponse.getResult() == DocWriteResponse.Result.DELETED) {
            log.warn("更新操作里文档 {} 被删除，请检查", indexDoc);
        } else if (updateResponse.getResult() == DocWriteResponse.Result.NOOP) {
            log.warn("文档 {} 未做任何更新操作，请检查", indexDoc);
        }
    }

    private void insertDoc(String docId, ProductESVo productESVo) throws IOException {
        IndexRequest indexRequest = new IndexRequest(indexName);
        indexRequest.id(docId);
        String productJson = JSONObject.toJSONString(productESVo);
        indexRequest.source(productJson, XContentType.JSON);
        IndexResponse indexResponse =
                restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        if (indexResponse != null) {
            String id = indexResponse.getId();
            if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
                log.info("新增文档成功,id = {}", id);
            } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                log.warn("新增转为覆盖文档成功,id = {}", id);
            }
        }
    }

}
