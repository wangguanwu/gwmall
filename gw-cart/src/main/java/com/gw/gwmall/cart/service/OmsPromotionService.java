package com.gw.gwmall.cart.service;

import com.gw.gwmall.cart.domain.CartPromotionItem;
import com.gw.gwmall.cart.model.OmsCartItem;

import java.util.List;

/**
 * 促销管理Service
 */
public interface OmsPromotionService {
    /**
     * 计算购物车中的促销活动信息
     * @param cartItemList 购物车
     */
    List<CartPromotionItem> calcCartPromotion(List<OmsCartItem> cartItemList);
}
