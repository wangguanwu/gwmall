package com.gw.gwmall.cart.controller;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.cart.dao.PortalProductDao;
import com.gw.gwmall.cart.domain.*;
import com.gw.gwmall.cart.service.PmsProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品详情信息管理
 **/
@RestController
@Api(tags = "PortalProductController", description = "商品查询查看")
@RequestMapping("/pms")
public class PortalProductController {

    @Autowired
    private PmsProductService pmsProductService;

    @Autowired
    private PortalProductDao portalProductDao;

    @ApiOperation(value = "根据商品id获取商品详情#功能需要做QPS优化")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flashPromotionId",value = "秒杀活动ID",paramType = "query",dataType = "long"),
            @ApiImplicitParam(name = "flashPromotionSessionId",value = "活动场次ID,例如:12点场",paramType = "query",dataType = "long")
    })
    @RequestMapping(value = "/productInfo/{id}", method = RequestMethod.GET)
    public CommonResult getProductInfo(@PathVariable Long id) {
        PmsProductParam pmsProductParam=pmsProductService.getProductInfo(id);
        return CommonResult.success(pmsProductParam);
    }


    @ApiOperation(value = "根据商品Id获取购物车商品的信息")
    @RequestMapping(value = "/cartProduct/{productId}", method = RequestMethod.GET)
    public  CommonResult<CartProduct> getCartProduct(@PathVariable("productId") Long productId){
        CartProduct cartProduct = portalProductDao.getCartProduct(productId);
        return CommonResult.success(cartProduct);
    }

    @ApiOperation(value = "根据商品Ids获取促销商品信息")
    @RequestMapping(value = "/getPromotionProductList", method = RequestMethod.GET)
    public CommonResult<List<PromotionProduct>> getPromotionProductList(@RequestParam("productIds") List<Long> ids){
        List<PromotionProduct> promotionProducts = portalProductDao.getPromotionProductList(ids);
        return CommonResult.success(promotionProducts);
    }

    @ApiOperation("当前秒杀活动场-产品列表#需要做QPS优化")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flashPromotionId", value = "秒杀活动ID", required = true, paramType = "query", dataType = "integer"),
            @ApiImplicitParam(name = "flashPromotionSessionId", value = "秒杀活动时间段ID", required = true, paramType = "query", dataType = "integer")})
    @GetMapping("/flashPromotion/productList")
    public CommonResult<List<FlashPromotionProduct>> getProduct(
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            //当前秒杀活动主题ID
            @RequestParam(value = "flashPromotionId") Long flashPromotionId,
            //当前秒杀活动场次ID
            @RequestParam(value = "flashPromotionSessionId") Long flashPromotionSessionId){
        return CommonResult.success(pmsProductService.getFlashProductList(pageSize,pageNum,flashPromotionId,flashPromotionSessionId));
    }


    @ApiOperation(value = "获取当前日期所有活动场次#需要做QPS优化",notes = "示例：10:00场,13:00场")
    @GetMapping("/flashPromotion/getSessionTimeList")
    public CommonResult<List<FlashPromotionSessionExt>> getSessionTimeList() {
        return CommonResult.success(pmsProductService.getFlashPromotionSessionList());
    }

    /**
     * 获取首页秒杀商品
     * @return
     */
    @GetMapping("/flashPromotion/getHomeSecKillProductList")
    public CommonResult<List<FlashPromotionProduct>> getHomeSecKillProductList(){
        return CommonResult.success(pmsProductService.getHomeSecKillProductList());
    }

}
