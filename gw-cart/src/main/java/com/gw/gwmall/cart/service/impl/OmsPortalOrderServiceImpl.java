package com.gw.gwmall.cart.service.impl;

import com.gw.gwmall.common.exception.BusinessException;
import com.gw.gwmall.cart.domain.CartPromotionItem;
import com.gw.gwmall.cart.domain.ConfirmOrderResult;
import com.gw.gwmall.cart.feignapi.ums.UmsMemberFeignApi;
import com.gw.gwmall.cart.model.*;
import com.gw.gwmall.cart.service.OmsCartItemService;
import com.gw.gwmall.cart.service.OmsPortalOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 前台订单管理Service
 */
@Service
@Slf4j
public class OmsPortalOrderServiceImpl implements OmsPortalOrderService {

    @Autowired
    private UmsMemberFeignApi umsMemberFeignApi;

    @Autowired
    private OmsCartItemService cartItemService;

    /**
     * 确认选择购买的商品
     * @param itemIds
     *        选择的购物车商品
     * @return
     */
    @Override
    public ConfirmOrderResult generateConfirmOrder(List<Long> itemIds,Long memberId) throws BusinessException {
        ConfirmOrderResult result = new ConfirmOrderResult();
        /*获取购物车信息*/

        //List<CartPromotionItem> cartPromotionItemList = cartFeignApi.listSelectedPromotion(itemIds,memberId);
//        List<CartPromotionItem> cartPromotionItemList = MockService.listSelectedPromotion(itemIds,memberId);
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listSelectedPromotion(memberId,itemIds);
        result.setCartPromotionItemList(cartPromotionItemList);

        /* 微服务调用获取用户收货地址列表 */
        List<UmsMemberReceiveAddress> memberReceiveAddressList = umsMemberFeignApi.list().getData();
//        result.setMemberReceiveAddressList(MockService.getUmsMemberReceiveAddress());
        result.setMemberReceiveAddressList(memberReceiveAddressList);

        //TODO 此处可加入其他微服务调用，比如用户积分、用户本人优惠劵等
        result.setMemberIntegration(0);

        //计算总金额、活动优惠、应付金额
        ConfirmOrderResult.CalcAmount calcAmount = calcCartAmount(cartPromotionItemList);
        result.setCalcAmount(calcAmount);
        return result;
    }

    /**
     * 计算购物车中商品的价格
     */
    private ConfirmOrderResult.CalcAmount calcCartAmount(List<CartPromotionItem> cartPromotionItemList) {
        ConfirmOrderResult.CalcAmount calcAmount = new ConfirmOrderResult.CalcAmount();
        calcAmount.setFreightAmount(new BigDecimal(0));
        BigDecimal totalAmount = new BigDecimal("0");
        BigDecimal promotionAmount = new BigDecimal("0");
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            totalAmount = totalAmount.add(cartPromotionItem.getPrice().multiply(new BigDecimal(cartPromotionItem.getQuantity())));
            if (null!=cartPromotionItem.getReduceAmount()) {
                promotionAmount = promotionAmount.add(cartPromotionItem.getReduceAmount().multiply(new BigDecimal(cartPromotionItem.getQuantity())));
            }
        }
        calcAmount.setTotalAmount(totalAmount);
        calcAmount.setPromotionAmount(promotionAmount);
        calcAmount.setPayAmount(totalAmount.subtract(promotionAmount));
        return calcAmount;
    }

}
