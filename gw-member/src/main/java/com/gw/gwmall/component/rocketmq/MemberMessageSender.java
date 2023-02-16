package com.gw.gwmall.component.rocketmq;

import com.alibaba.nacos.common.utils.JacksonUtils;
import com.gw.gwmall.common.domain.UserCoupon;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @author guanwu
 * @created on 2023-02-14 19:04:42
 **/

@Component
public class MemberMessageSender {

    @Value("${rocketmq.gwmall.transCouponTopic}")
    private String transCouponTopic;

    private static final String couponTag = "give-away-coupon";

    private final RocketMQTemplate rocketMQTemplate;

    public MemberMessageSender(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    public boolean sendTransactionMessage(UserCoupon couponInfo) {
        String destination = transCouponTopic+":"+ couponTag;
        Message<String> message = MessageBuilder.withPayload(JacksonUtils.toJson(couponInfo))
                .build();
        TransactionSendResult transactionSendResult = rocketMQTemplate.sendMessageInTransaction(destination, message,
                couponInfo.getMemberId());
        return transactionSendResult.getSendStatus() == SendStatus.SEND_OK;
    }
}
