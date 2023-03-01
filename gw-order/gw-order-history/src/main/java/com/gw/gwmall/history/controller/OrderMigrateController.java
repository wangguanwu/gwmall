package com.gw.gwmall.history.controller;

import com.gw.gwmall.history.service.MigrateCentreService;
import com.gw.gwmall.history.service.impl.OrderConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单迁移管理Controller
 */
@Slf4j
@RestController
@Api(tags = "OrderMigrateController",description = "订单迁移管理")
@RequestMapping("/order/migrate")
public class OrderMigrateController {

    @Autowired
    private MigrateCentreService migrateCentreService;

    @ApiOperation("指定数据表执行数据迁移")
    @RequestMapping(value = "/specificTableMigrate",method = {RequestMethod.POST,RequestMethod.GET})
    public String migrateSpecificTable(@RequestParam int tableNo){
        return migrateCentreService.migrateSingleOrderTable(tableNo);
    }

    @ApiOperation("全部数据表进行迁移")
    @RequestMapping(value = "/migrateTables",method = {RequestMethod.POST,RequestMethod.GET})
    public String migrateTables(){
        return migrateCentreService.migrateAllOrderTables();
    }

    @ApiOperation("停止迁移")
    @RequestMapping(value = "/stopMigrate",method = {RequestMethod.POST,RequestMethod.GET})
    public String stopRoundMigrate(){
        migrateCentreService.stop();
        return OrderConstant.MIGRATE_SUCCESS;
    }

    /**
     * 接口已废弃，迁移开始时，会自动调用recover方法恢复迁移
     * @return
     */
    @Deprecated
    @ApiOperation("恢复迁移")
    @RequestMapping(value = "/recoverMigrate",method = {RequestMethod.POST,RequestMethod.GET})
    public String recoverMigrate(){
        migrateCentreService.recover();
        return OrderConstant.MIGRATE_SUCCESS;
    }
}
