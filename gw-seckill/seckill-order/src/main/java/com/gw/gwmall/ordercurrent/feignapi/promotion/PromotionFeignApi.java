package com.gw.gwmall.ordercurrent.feignapi.promotion;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
* @desc: 类的描述:秒杀库存管理
*/
@FeignClient(name = "gw-promotion",path = "/seckill")
public interface PromotionFeignApi {

    /*减库存*/
    @RequestMapping(value = "/descStock", method = RequestMethod.GET)
    @ResponseBody
    public Integer descStock(@RequestParam("id") Long flashPromotionRelationId,
                             @RequestParam("stock") Integer stock);

    /*增加库存*/
    @RequestMapping(value = "/incStock", method = RequestMethod.GET)
    @ResponseBody
    public Integer incStock(@RequestParam("id") Long flashPromotionRelationId,
                             @RequestParam("stock") Integer stock);
}
