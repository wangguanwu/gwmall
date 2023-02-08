package com.gw.gwmall.ordercurrent.component;

import com.gw.gwmall.ordercurrent.domain.MqCancelOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * 取消订单消息的发出者
 */
@Slf4j
@Component
public class CancelOrderSender implements InitializingBean {
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void sendMessage(MqCancelOrder mqCancelOrder, final long delayTimes){
        //给延迟队列发送消息
        // messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
        rocketMQTemplate.syncSend("mall.order.cancel.ttl",
                MessageBuilder.withPayload(mqCancelOrder).build(),5000,16);

        log.info("send orderId:{}",mqCancelOrder.getOrderId());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //设置消息转换器
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
    }
}
