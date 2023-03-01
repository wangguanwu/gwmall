package com.gw.gwmall.portal.feignapi.pms;

import com.gw.gwmall.FlashPromotionProduct;
import com.gw.gwmall.common.api.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @desc: 远程调用获取秒杀商品首页列表
 **/
@FeignClient(name = "gw-product",path = "/pms")
public interface PmsProductFeignApi {
    
    @RequestMapping(value = "/flashPromotion/getHomeSecKillProductList", method = RequestMethod.GET)
    @ResponseBody
    CommonResult<List<FlashPromotionProduct>> getHomeSecKillProductList();
}
