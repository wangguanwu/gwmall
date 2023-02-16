package com.gw.gwmall.promotion.component.rocketmq;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.common.domain.UserCoupon;
import com.gw.gwmall.common.util.JacksonUtils;
import com.gw.gwmall.promotion.service.UserCouponService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author guanwu
 * @created on 2023-02-14 17:30:35
 *
 * 赠送用户优惠券消费者
 **/


@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "${rocketmq.gwmall.couponConsumerGroup}",
        topic = "${rocketmq.gwmall.transCouponTopic}")
public class RocketMqCouponListener implements RocketMQListener<UserCoupon> {

    @Resource
    private UserCouponService userCouponService;

    @Override
    public void onMessage(UserCoupon coupon) {
        log.info("[RocketMq] 消费赠送优惠券消息...{}", JacksonUtils.toJson(coupon));
        CommonResult<String> result = userCouponService.activelyGet(coupon.getCouponId(), coupon.getMemberId(), coupon.getNick(),
                coupon.getType());
        log.info("优惠券信息结果: {}", JacksonUtils.toJson(result));

    }
}
