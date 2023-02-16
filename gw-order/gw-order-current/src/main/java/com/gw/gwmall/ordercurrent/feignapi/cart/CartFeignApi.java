package com.gw.gwmall.ordercurrent.feignapi.cart;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.ordercurrent.domain.CartPromotionItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @desc: 调用购物车服务
*/
@FeignClient(name = "gw-cart",path = "/cart")
public interface CartFeignApi {


//    public String getSegmentId(@PathVariable("key") String key) ;
//    @RequestMapping(value = "/api/segment/get/{key}")
//    public List<CartPromotionItem> listSelectedPromotion(List<Long> itemIds,Long memberId);

    @RequestMapping(value = "/list/selectedpromotion", method = RequestMethod.POST)
    List<CartPromotionItem> listSelectedPromotion(@RequestBody List<Long> itemIds);

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    CommonResult<Integer> cartDelete(@RequestParam("ids") List<Long> ids);

}
