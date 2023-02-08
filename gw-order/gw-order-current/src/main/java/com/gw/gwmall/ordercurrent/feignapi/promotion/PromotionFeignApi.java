package com.gw.gwmall.ordercurrent.feignapi.promotion;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.ordercurrent.domain.CartPromotionItem;
import com.gw.gwmall.ordercurrent.domain.SmsCouponHistoryDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @desc: 调用优惠券服务
 */
@FeignClient(name = "gw-promotion", path = "/coupon")
public interface PromotionFeignApi {

    /*"type", value = "使用可用:0->不可用；1->可用"*/
    @RequestMapping(value = "/listCart", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<List<SmsCouponHistoryDetail>> listCartCoupons(@RequestParam(value="type") Integer type,
                    @RequestBody List<CartPromotionItem> cartPromotionItemList);
}
