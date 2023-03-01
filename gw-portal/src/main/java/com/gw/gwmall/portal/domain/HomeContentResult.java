package com.gw.gwmall.portal.domain;

import com.gw.gwmall.FlashPromotionProduct;
import com.gw.gwmall.model.CmsSubject;
import com.gw.gwmall.model.PmsBrand;
import com.gw.gwmall.model.PmsProduct;
import com.gw.gwmall.promotion.model.SmsHomeAdvertise;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 首页内容返回信息封装
 */
@Getter
@Setter
public class HomeContentResult {
    //轮播广告
    private List<SmsHomeAdvertise> advertiseList;
    //推荐品牌
    private List<PmsBrand> brandList;

    private List<FlashPromotionProduct> homeFlashPromotion;
    //新品推荐
    private List<PmsProduct> newProductList;
    //人气推荐
    private List<PmsProduct> hotProductList;
    //推荐专题
    private List<CmsSubject> subjectList;
}
