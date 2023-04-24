package com.gw.gwmall.ordercurrent.domain;

import com.gw.gwmall.ordercurrent.model.UmsMemberReceiveAddress;

/**
 * 生成秒杀订单时传入的参数
 */
public class SecKillOrderParam {

    /*可用于避免重复生成订单*/
    private Long orderId;
    private Long orderItemId;

    /*秒杀活动的ID*/
    private Long flashPromotionId;

    //收货地址
    private UmsMemberReceiveAddress memberReceiveAddress;
    //支付方式
    private Integer payType;
    //选择购买的秒杀商品
    private Long productId;

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public UmsMemberReceiveAddress getMemberReceiveAddress() {
        return memberReceiveAddress;
    }

    public void setMemberReceiveAddress(UmsMemberReceiveAddress memberReceiveAddress) {
        this.memberReceiveAddress = memberReceiveAddress;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Long getFlashPromotionId() {
        return flashPromotionId;
    }

    public void setFlashPromotionId(Long flashPromotionId) {
        this.flashPromotionId = flashPromotionId;
    }

    @Override
    public String toString() {
        return "SecKillOrderParam{" +
                "orderId=" + orderId +
                ", orderItemId=" + orderItemId +
                ", flashPromotionId=" + flashPromotionId +
                ", payType=" + payType +
                ", productId=" + productId +
                '}';
    }
}
