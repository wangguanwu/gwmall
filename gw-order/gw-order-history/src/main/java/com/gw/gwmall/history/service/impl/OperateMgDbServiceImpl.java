package com.gw.gwmall.history.service.impl;

import com.mongodb.client.result.UpdateResult;
import com.gw.gwmall.history.domain.MongoOrderId;
import com.gw.gwmall.history.domain.OmsOrderDetail;
import com.gw.gwmall.history.service.OperateMgDbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class OperateMgDbServiceImpl implements OperateMgDbService {

    private static final String ORDER_MAX_ID_KEY = "tableName";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void saveToMgDb(List<OmsOrderDetail> orders, long curMaxOrderId,String tableName) {
        log.info("准备将表{}数据迁移入MongoDB，参数curMaxOrderId = {}",tableName,curMaxOrderId);
        mongoTemplate.insert(orders,OmsOrderDetail.class);
        /*记录本次迁移的最大订单ID，下次迁移时需要使用*/
        Query query = new Query(Criteria.where(ORDER_MAX_ID_KEY).is(tableName));
        Update update = new Update();
        update.set(ORDER_MAX_ID_KEY,curMaxOrderId);
        UpdateResult updateResult = mongoTemplate.upsert(query,update,MongoOrderId.class);
        log.info("已记录表{}本次迁移最大订单ID = {}",tableName,curMaxOrderId);
    }

    @Override
    public long getMaxOrderId(String tableName) {
        Query query = new Query(Criteria.where(ORDER_MAX_ID_KEY).is(tableName));
        MongoOrderId mongoOrderId = mongoTemplate.findOne(query, MongoOrderId.class);
//        MongoOrderId mongoOrderId = mongoTemplate.findOne(query,MongoOrderId.class);
        long result = mongoOrderId == null ? 0 : mongoOrderId.getMaxOrderId();
        log.info("表{}本次迁移起始订单ID = {}",tableName,result);
        return result;
    }
}
