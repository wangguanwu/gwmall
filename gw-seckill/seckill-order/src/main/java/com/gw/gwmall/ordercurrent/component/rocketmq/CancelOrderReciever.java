package com.gw.gwmall.ordercurrent.component.rocketmq;


import com.gw.gwmall.ordercurrent.feignapi.promotion.PromotionFeignApi;
import com.gw.gwmall.ordercurrent.mapper.OmsOrderMapper;
import com.gw.gwmall.ordercurrent.service.SecKillOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 消费监听rocketmq-秒杀订单超时消息
 */
@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = "${rocketmq.gwmall.cancelGroupSk}",
        topic = "${rocketmq.gwmall.scheduleTopicSk}")
public class CancelOrderReciever implements RocketMQListener<String> {

    @Resource
    private PromotionFeignApi promotionFeignApi;

    @Resource
    private SecKillOrderService secKillOrderService;

    @Resource
    private OmsOrderMapper orderMapper;

    /**
     * 延时消息,取消超时订单
     * @param cancelId
     */
    @Override
    public void onMessage(String cancelId) {
        if(StringUtils.isEmpty(cancelId)){
            return;
        }
        String[] content = cancelId.split(":");
        Long orderId = Long.parseLong(content[0]);
        Long flashPromotionRelationId = Long.parseLong(content[1]);
        Long productId = Long.parseLong(content[2]);
        //应该有一个写数据库操作

        try {
            secKillOrderService.cancelSecKillOrder(orderId, flashPromotionRelationId, productId);
            //这里挂了怎么办，怎么保证服务幂等性

        } catch (Exception e) {
            log.error("订单取消异常 : 还原库存失败，please check:{}",e.getMessage(), e.getCause());
            throw new RuntimeException();//抛异常出去,rocketmq会重新投递
        }

    }

}
