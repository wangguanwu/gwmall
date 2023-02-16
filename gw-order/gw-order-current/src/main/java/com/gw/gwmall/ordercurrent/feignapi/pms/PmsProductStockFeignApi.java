package com.gw.gwmall.ordercurrent.feignapi.pms;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.ordercurrent.domain.CartPromotionItem;
import com.gw.gwmall.ordercurrent.domain.StockChanges;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
* @desc: 类的描述: 订单服务用于调用商品服务锁定库存
*/
@FeignClient(value = "gw-product",path = "/stock")
public interface PmsProductStockFeignApi {


    @RequestMapping("/lockStock")
    CommonResult lockStock(@RequestBody List<CartPromotionItem> cartPromotionItemList);

    @RequestMapping("/reduceStock")
    CommonResult reduceStock(@RequestBody List<StockChanges> stockChangesList);

    @RequestMapping("/recoverStock")
    CommonResult recoverStock(@RequestBody List<StockChanges> stockChangesList);
}
