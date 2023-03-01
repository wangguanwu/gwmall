package com.gw.gwmall.portal.feignapi.promotion;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.portal.domain.HomeContentResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
* @desc: 远程调用获取首页显示内容，包括推荐和秒杀等
*/
@FeignClient(name = "gw-promotion",path = "/recommend")
public interface PromotionFeignApi {

    /*推荐内容类型:0->全部；1->品牌；2->新品推荐；3->人气推荐;4->轮播广告*/
    @RequestMapping(value = "/content", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<HomeContentResult> content(@RequestParam(value = "getType") int getType);

}
