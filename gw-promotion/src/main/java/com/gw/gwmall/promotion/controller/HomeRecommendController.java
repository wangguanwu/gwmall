package com.gw.gwmall.promotion.controller;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.promotion.domain.HomeContentResult;
import com.gw.gwmall.promotion.service.HomePromotionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 首页推荐管理Controller
 */
@Controller
@Api(tags = "HomeRecommendController", description = "品牌和产品推荐内容")
@RequestMapping("/recommend")
public class HomeRecommendController {
    @Autowired
    private HomePromotionService homePromotionService;

    @ApiOperation("首页品牌和产品推荐")
    @ApiImplicitParam(name = "getType", value = "推荐内容类型:0->全部；1->品牌；2->新品推荐；3->人气推荐;4->轮播广告",
            allowableValues = "0,1,2,3,4", paramType = "query", dataType = "integer")
    @RequestMapping(value = "/content", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<HomeContentResult> content(@RequestParam int getType) {
        HomeContentResult contentResult = homePromotionService.content(getType);
        return CommonResult.success(contentResult);
    }




}
