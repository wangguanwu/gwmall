package com.gw.gwmall.history.service;

/**
 * 订单迁移调度中心
 */
public interface MigrateCentreService {
    /*单表迁移*/
    String migrateSingleTableOrders(int tableNo);

    /*全部迁移*/
    String migrateTablesOrders();

    /*停止迁移*/
    public void stopMigrate();

    /*恢复迁移*/
    public void recoverMigrate();
}
