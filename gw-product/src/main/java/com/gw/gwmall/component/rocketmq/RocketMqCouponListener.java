package com.gw.gwmall.component.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * @author guanwu
 * @created on 2023-02-14 17:30:35
 *
 * 赠送用户优惠券消费者
 **/


@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "${rocketmq.gwmall.couponConsumerGroup}", topic = "${rocketmq.gwmember.transCouponTopic}")
public class RocketMqCouponListener implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        String[] messageArray = message.split(",");
        String userId = messageArray[0],
                couponId = messageArray[1];

    }
}
