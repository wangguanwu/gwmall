package com.gw.gwmall.ordercurrent.component.rocketmq.pay;

import com.gw.gwmall.ordercurrent.service.OmsPortalOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 消费监听rocketmq-订单超时消息
 */
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "CancelOrderGroup", topic = "${rocketmq.gwmall.asyncOrderTopic}")
public class RocketMqCancelOrderReciever implements RocketMQListener<String> {

    @Autowired
    private OmsPortalOrderService omsPortalOrderService;

    /**
     * 延时消息,取消超时订单
     * @param message orderId:memberId
     */
    @Override
    public void onMessage(String message) {
        log.info("监控到订单支付超时事件，取消超时订单：message = "+message);
        if(StringUtils.isEmpty(message)){
            return;
        }
        Long orderId = Long.parseLong(message.split(":")[0]);
        Long memberId = Long.parseLong(message.split(":")[1]);
        try {
            //取消的订单,释放锁定的库存
            omsPortalOrderService.cancelOrder(orderId,memberId);
            //取消的订单-还原缓存库存
//            secKillOrderService.incrRedisStock(productId);
            //这一步取消。订单超时不再更新Redis中的库存。未支付的订单就不再重新参与秒杀了。等待返场活动处理。
        } catch (Exception e) {
            log.error("订单取消异常 : 还原库存失败，please check:{}",e.getMessage(),e.getCause());
            throw new RuntimeException();//抛异常出去,rocketmq会重新投递
        }
    }
}
