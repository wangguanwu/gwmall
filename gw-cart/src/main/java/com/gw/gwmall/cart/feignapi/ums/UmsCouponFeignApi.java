package com.gw.gwmall.cart.feignapi.ums;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.cart.domain.CartPromotionItem;
import com.gw.gwmall.cart.domain.SmsCouponHistoryDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* 会员优惠卷服务

*/
@FeignClient(name = "gw-promotion",path = "/coupon")
public interface UmsCouponFeignApi {

    @RequestMapping(value = "/listCart", method = RequestMethod.POST)
    @ResponseBody
    CommonResult<List<SmsCouponHistoryDetail>> listCartCoupons(@RequestParam("type") Integer type,
                                                               @RequestBody List<CartPromotionItem> cartPromotionItemList);

}
