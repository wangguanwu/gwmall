package com.gw.gwmall.service;


import com.gw.gwmall.domain.*;
import com.gw.gwmall.model.PmsBrand;
import com.gw.gwmall.model.PmsProduct;

import java.util.List;

/**
 * @description:
 **/
public interface PmsProductService {
    /**
     * add by yangguo
     * 获取商品详情信息
     * @param id 产品ID
     */
    PmsProductParam getProductInfo(Long id);

    /**
     * 批量获取产品信息
     * @param productIdList
     * @return
     */
    List<PmsProduct> getProductBatch(List<Long> productIdList);

    /**
     * 批量获取推荐品牌
     * @param brandIdList
     * @return
     */
    List<PmsBrand> getRecommandBrandList(List<Long> brandIdList);

    /**
     * 获取秒杀商品
     * @param pageSize 页大小
     * @param pageNum 页号
     * @param flashPromotionId 秒杀活动ID，关联秒杀活动设置
     * @param sessionId 场次活动ID，for example：13:00-14:00场等
     */
    List<FlashPromotionProduct> getFlashProductList(Integer pageSize, Integer pageNum, Long flashPromotionId, Long sessionId);

    /**
     * 获取当前日期秒杀活动所有场次for example：13:00-14:00场等
     */
    List<FlashPromotionSessionExt> getFlashPromotionSessionList();

    List<FlashPromotionProduct> getHomeSecKillProductList();

    CartProduct getCartProduct(Long productId);

    List<PromotionProduct> getPromotionProductList(List<Long> ids);

    /**
     * 查找出所有的产品ID
     * @return
     */
    List<Long> getAllProductId();
}
