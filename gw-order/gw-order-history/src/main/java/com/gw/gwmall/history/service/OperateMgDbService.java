package com.gw.gwmall.history.service;

import com.gw.gwmall.history.domain.OmsOrderDetail;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 订单迁移mongodb数据库管理Service接口
 */
public interface OperateMgDbService {

    @Transactional(transactionManager = "mongoTransactionManager",rollbackFor = Throwable.class)
    void saveToMgDb(List<OmsOrderDetail> orders,long curMaxOrderId,String tableName);

    long getMaxOrderId(String tableName);

}
