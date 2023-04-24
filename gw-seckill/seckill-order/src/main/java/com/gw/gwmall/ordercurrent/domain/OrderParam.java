package com.gw.gwmall.ordercurrent.domain;

import java.util.List;

/**
 * 生成订单时传入的参数
 */
public class OrderParam {

    /*可用于避免重复生成订单*/
    private Long orderId;
    //收货地址id
    private Long memberReceiveAddressId;
    //优惠券id
    private Long couponId;
    //使用的积分数
    private Integer useIntegration;
    //支付方式
    private Integer payType;
    //选择购买的购物车商品
    private List<Long> itemIds;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<Long> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Long> itemIds) {
        this.itemIds = itemIds;
    }

    public Long getMemberReceiveAddressId() {
        return memberReceiveAddressId;
    }

    public void setMemberReceiveAddressId(Long memberReceiveAddressId) {
        this.memberReceiveAddressId = memberReceiveAddressId;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getUseIntegration() {
        return useIntegration;
    }

    public void setUseIntegration(Integer useIntegration) {
        this.useIntegration = useIntegration;
    }

    @Override
    public String toString() {
        return "OrderParam{" +
                "orderId=" + orderId +
                ", memberReceiveAddressId=" + memberReceiveAddressId +
                ", couponId=" + couponId +
                ", useIntegration=" + useIntegration +
                ", payType=" + payType +
                ", itemIds=" + itemIds +
                '}';
    }
}
