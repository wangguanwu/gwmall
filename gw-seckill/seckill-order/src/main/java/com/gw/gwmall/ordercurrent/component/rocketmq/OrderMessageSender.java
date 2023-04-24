package com.gw.gwmall.ordercurrent.component.rocketmq;

import com.gw.gwmall.ordercurrent.domain.OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderMessageSender {

    @Value("${rocketmq.gwmall.scheduleTopicSk}")
    private String scheduleTopic;

    @Value("${rocketmq.gwmall.asyncOrderTopicSk}")
    private String asyncOrderTopic;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送延时订单
     * messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     * @param cancelId
     *      秒杀订单ID orderId:promotionId 秒杀活动ID
     * @return
     */
    public boolean sendTimeOutOrderMessage(String cancelId){
        Message<String> message = MessageBuilder.withPayload(cancelId)
                .setHeader(RocketMQHeaders.KEYS, cancelId)
                .build();
        SendResult result = rocketMQTemplate.syncSend(scheduleTopic, message,5000,3);
        return SendStatus.SEND_OK == result.getSendStatus();
    }

    /**发送创建订单消息*/
    public boolean sendCreateOrderMsg(OrderMessage message){
        SendResult result = rocketMQTemplate.syncSend(asyncOrderTopic,message);
        return SendStatus.SEND_OK == result.getSendStatus();
    }
}
