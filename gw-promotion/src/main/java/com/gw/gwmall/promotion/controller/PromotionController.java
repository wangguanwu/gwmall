package com.gw.gwmall.promotion.controller;


import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.promotion.domain.CartPromotionItem;
import com.gw.gwmall.promotion.domain.SmsCouponHistoryDetail;
import com.gw.gwmall.promotion.model.SmsCouponHistory;
import com.gw.gwmall.promotion.service.UserCouponService;
import com.gw.gwmall.promotion.service.impl.ConstantPromotion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 优惠券管理Controller
 */
@Controller
@Api(tags = "PromotionController", description = "优惠券统一管理")
@RequestMapping("/coupon")
@Slf4j
public class PromotionController {
    @Autowired
    private UserCouponService userCouponService;

    @ApiOperation("用户领取指定优惠券")
    @RequestMapping(value = "/add/{couponId}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<String> userActivelyGet(@PathVariable("couponId") Long couponId,
                                        @RequestHeader(value = "memberId") Long memberId,
                                        @RequestHeader(value = "nickName") String nickName,
                                        @RequestParam(value = "getType", defaultValue = ""+ConstantPromotion.USER_COUPON_GET_TYPE_GIFT) Integer getType) {
        return userCouponService.activelyGet(couponId,memberId,nickName, getType);
    }

    @ApiOperation("获取用户优惠券列表")
    @ApiImplicitParam(name = "useStatus", value = "优惠券筛选类型:0->未使用；1->已使用；2->已过期",
            allowableValues = "0,1,2", paramType = "query", dataType = "integer")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<SmsCouponHistory>> listUserCoupons(
            @RequestParam(value = "useStatus", required = false) Integer useStatus,
            @RequestHeader("memberId") Long memberId) {

        //模拟异常
        if(memberId ==2){
            throw new IllegalArgumentException("非法参数异常");
        }
        List<SmsCouponHistory> couponHistoryList = userCouponService.listCoupons(useStatus,memberId);
        return CommonResult.success(couponHistoryList);
    }

//    @Autowired
//    private PluginAdapter pluginAdapter;
//
//    @RequestMapping(value = "/gray/{value}", method = RequestMethod.GET)
//    @ResponseBody
//    public String gray(@PathVariable(value = "value") String value) {
//        value = pluginAdapter.getPluginInfo(value);
//        log.info("调用路径：{}", value);
//        return value;
//    }


    @ApiOperation("获取用户购物车商品的相关优惠券")
    @ApiImplicitParam(name = "type", value = "使用可用:0->不可用；1->可用",
            defaultValue = "1", allowableValues = "0,1", paramType = "query", dataType = "integer")
    @RequestMapping(value = "/listCart", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<List<SmsCouponHistoryDetail>> listCartCoupons(@RequestParam Integer type,
                                                                      @RequestBody List<CartPromotionItem> cartPromotionItemList,
                                                                      @RequestHeader("memberId")Long memberId) {

        List<SmsCouponHistoryDetail> couponHistoryList = userCouponService.listCart(cartPromotionItemList, type,memberId);
        return CommonResult.success(couponHistoryList);
    }
}
