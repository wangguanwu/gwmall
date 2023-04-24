package com.gw.gwmall.cart.service;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.common.exception.BusinessException;

/**
 * @description 秒杀订单确认服务
 **/
public interface SecKillConfirmOrderService {

    /**
     * 生成秒杀确认单
     * @param productId
     * @param memberId
     * @param token
     * @param flashPromotionId
     * @return
     */
    CommonResult generateConfirmSecKillOrder(Long productId
            , Long memberId, String token, Long flashPromotionId) throws BusinessException;

}
