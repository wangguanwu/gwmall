package com.gw.gwmall.ordercurrent.controller;/*
package com.tuling.tulingmall.controller;

import com.tuling.tulingmall.common.api.CommonResult;
import com.tuling.tulingmall.service.impl.Transaction2PcServiceImpl;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.apache.shardingsphere.transaction.core.TransactionTypeHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class Transaction2PcController {
    @Autowired
    private Transaction2PcServiceImpl service;

    @GetMapping("/sharding/2pc_transaction")
    public CommonResult insertIn2Pc(){
        //TransactionTypeHolder.set(TransactionType.XA);
        return service.insertOrder();
    }

    @GetMapping("/sharding/select")
    public CommonResult select(@RequestHeader("memberId") Long memberId){
        return service.selectOrder(memberId);
    }
}
*/
