package com.gw.gwmall.promotion.controller;


import com.gw.gwmall.FlashPromotionProduct;
import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.promotion.dao.MiaoShaStockDao;
import com.gw.gwmall.promotion.service.HomePromotionService;
import com.gw.gwmall.promotion.service.ISecKillStaticHtmlService;
import com.gw.gwmall.promotion.service.impl.ConstantPromotion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 秒杀管理Controller
 */
@Slf4j
@Controller
@Api(tags = "SecKillController", description = "秒杀管理")
@RequestMapping("/seckill")
public class SecKillController {

    @Autowired
    private HomePromotionService homePromotionService;
    @Autowired
    private MiaoShaStockDao miaoShaStockDao;
    @Autowired
    private ISecKillStaticHtmlService secKillStaticHtmlService;

    /*获得秒杀内容*/
    @ApiOperation("获取秒杀产品")
    @RequestMapping(value = "/getHomeSecKillProductList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<FlashPromotionProduct>> getHomeSecKillProductList(
            @RequestParam(required = false,defaultValue = "-1") long secKillId,
            @RequestParam(required = false,defaultValue = "1") int status){
        List<FlashPromotionProduct> result = homePromotionService.secKillContent(secKillId,status);
        return CommonResult.success(result);
    }

    @ApiOperation("开启秒杀")
    @RequestMapping(value = "/openSecKill", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Integer> turnOnSecKill(@RequestParam long secKillId){
        int result = homePromotionService.turnOnSecKill(secKillId, ConstantPromotion.SECKILL_OPEN);
        try {
            if(secKillStaticHtmlService.deployHtml(secKillId) == ConstantPromotion.STATIC_HTML_FAILURE){
                return CommonResult.failed("发布秒杀静态页失败！");
            }
        } catch (Exception e) {
            log.error("发布秒杀静态产品页异常：",e);
            homePromotionService.turnOnSecKill(secKillId, ConstantPromotion.SECKILL_CLOSE);
            return CommonResult.failed(e.getMessage());
        }
        return CommonResult.success(result);
    }

    /*扣减库存 要防止库存超卖*/
    @ApiOperation("扣减库存")
    @RequestMapping(value = "/descStock", method = RequestMethod.GET)
    @ResponseBody
    public Integer descStock(@RequestParam("id") Long flashPromotionRelationId,
                             @RequestParam("stock") Integer stock){
        return miaoShaStockDao.descStock(flashPromotionRelationId,stock);
    }

    @ApiOperation("增加库存")
    @RequestMapping(value = "/incStock", method = RequestMethod.GET)
    @ResponseBody
    public Integer incStock(@RequestParam("id") Long flashPromotionRelationId,
                             @RequestParam("stock") Integer stock){
        return miaoShaStockDao.incStock(flashPromotionRelationId,stock);
    }

    @ApiOperation("查询库存")
    @RequestMapping(value = "/getStock", method = RequestMethod.GET)
    @ResponseBody
    public Integer getStock(@RequestParam("id") Long flashPromotionRelationId){
        return miaoShaStockDao.getStock(flashPromotionRelationId);
    }

}
