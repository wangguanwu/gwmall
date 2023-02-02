package com.gw.gwmall.promotion.service;




import com.gw.gwmall.FlashPromotionProduct;
import com.gw.gwmall.promotion.domain.HomeContentResult;

import java.util.List;

/**
 * 首页内容管理Service
 */
public interface HomePromotionService {

    /* 获取首页推荐品牌和产品*/
    HomeContentResult content(int getType);

    /*秒杀产品*/
    List<FlashPromotionProduct> secKillContent(long secKillId, int status);

    int turnOnSecKill(long secKillId,int status);

//    /**
//     * 首页商品推荐
//     */
//    List<PmsProduct> recommendProductList(Integer pageSize, Integer pageNum);
//
//    /**
//     * 获取商品分类
//     * @param parentId 0:获取一级分类；其他：获取指定二级分类
//     */
//    List<PmsProductCategory> getProductCateList(Long parentId);

//    /**
//     * 根据专题分类分页获取专题
//     * @param cateId 专题分类id
//     */
//    List<CmsSubject> getSubjectList(Long cateId, Integer pageSize, Integer pageNum);
}
