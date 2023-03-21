package com.gw.gwmall.component.rocketmq;

import com.gw.gwmall.common.util.JacksonUtils;
import com.gw.gwmall.common.vo.ESProductUpdateParam;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author guanwu
 * @created on 2023-03-13 16:33:04
 **/

@Component
public class CanalRocketMqSender {

    @Value("${rocketmq.gwmall.product-change-topic}")
    private String productTopic;

    @Value("${rocketmq.gwmall.promotion-change-topic}")
    private String promotionTopic;

    @Value("${rocketmq.gwmall.seckill-change-topic}")
    private String seckillTopic;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public boolean sendProductChangeMessage(List<ESProductUpdateParam> updateParams) {
        SendResult sendResult = rocketMQTemplate.syncSend(this.productTopic,
                MessageBuilder.withPayload(JacksonUtils.toJson(updateParams)).build(),
                5000);
        return sendResult.getSendStatus() == SendStatus.SEND_OK;
    }

}
