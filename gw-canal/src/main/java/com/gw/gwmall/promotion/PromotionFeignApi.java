package com.gw.gwmall.promotion;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.domain.FlashPromotionProduct;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
* @desc:
*/
@FeignClient(name = "gw-promotion",path = "/seckill")
public interface PromotionFeignApi {

    /*获得秒杀内容*/
    @RequestMapping(value = "/getHomeSecKillProductList", method = RequestMethod.GET)
    @ResponseBody
    CommonResult<List<FlashPromotionProduct>> getHomeSecKillProductList(
            @RequestParam(value = "secKillId") long secKillId,
            @RequestParam(value = "status") long status);
}
