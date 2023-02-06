package com.gw.gwmall.service;


import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.domain.CartPromotionItem;
import com.gw.gwmall.domain.StockChanges;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @description: 库存管理
 **/
public interface StockManageService {

    /**
     * 添加库存
     * @param productId
     * @param skuId
     * @param quanlity
     * @param miaosha
     * @return
     */
    Integer incrStock(Long productId, Long skuId, Integer quanlity, Integer miaosha, Long flashPromotionRelationId);

    /**
     * 减库存
     * @param productId
     * @param skuId
     * @param quanlity
     * @param miaosha
     * @return
     */
    Integer descStock(@RequestParam Long productId, Long skuId, Integer quanlity, Integer miaosha, Long flashPromotionRelationId);

    /**
     * 查询库存
     * @param productId
     * @param flashPromotionRelationId
     * @return
     */
    CommonResult<Integer> selectStock(Long productId, Long flashPromotionRelationId);

    /**
     * 方法实现说明:锁定库存
     */
    CommonResult<Boolean> lockStock(List<CartPromotionItem> cartPromotionItemList);

    CommonResult<Integer> reduceStock(List<StockChanges> stockChangesList);

    CommonResult<Boolean> recoverStock(List<StockChanges> stockChangesList);
}
