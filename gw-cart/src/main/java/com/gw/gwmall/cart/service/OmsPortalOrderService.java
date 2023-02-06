package com.gw.gwmall.cart.service;

import com.gw.gwmall.common.exception.BusinessException;
import com.gw.gwmall.cart.domain.ConfirmOrderResult;

import java.util.List;

/**
 * 前台订单管理Service
 */
public interface OmsPortalOrderService {

    ConfirmOrderResult generateConfirmOrder(List<Long> itemIds, Long memberId) throws BusinessException;

}

