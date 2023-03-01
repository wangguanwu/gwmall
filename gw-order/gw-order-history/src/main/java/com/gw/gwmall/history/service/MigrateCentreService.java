package com.gw.gwmall.history.service;

/**
 * 订单迁移调度中心
 */
public interface MigrateCentreService {
    /*单表迁移*/
    String migrateSingleOrderTable(int tableNo);

    /*全部迁移*/
    String migrateAllOrderTables();

    /*停止迁移*/
    void stop();

    /*恢复迁移*/
    void recover();
}
