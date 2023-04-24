package com.gw.gwmall.ordercurrent.component.rocketmq;//package com.tuling.tulingmall.component.rocketmq;

import com.gw.gwmall.common.constant.RedisKeyPrefixConst;
import com.gw.gwmall.ordercurrent.component.LocalCache;
import com.gw.gwmall.ordercurrent.domain.OrderMessage;
import com.gw.gwmall.ordercurrent.service.SecKillOrderService;

import com.gw.gwmall.rediscomm.util.RedisSingleUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.concurrent.TimeUnit;

/**
 * @description: 消费监听rocketmq-接受创建订单消息
 * 在DB中写入订单数据的部分，变为独立服务或者调用订单微服务更好
 **/
@Slf4j
@Component
@RocketMQMessageListener(topic = "${rocketmq.gwmall.asyncOrderTopicSk}",
        consumerGroup = "${rocketmq.gwmall.asyncOrderGroupSk}")
public class AscynCreateOrderReciever implements RocketMQListener<OrderMessage> {

    @Resource
    private SecKillOrderService secKillOrderService;

    @Resource
    private RedisSingleUtil redisStockUtil;

    @Resource
    private LocalCache<Object> cache;

    /**
     * 消费异步下单消息
     * @param orderMessage
     */
    @Override
    public void onMessage(OrderMessage orderMessage)  {
        log.info("listen the rocketmq message");
        Long memberId = orderMessage.getOrder().getMemberId();
        Long productId = orderMessage.getOrderItem().getProductId();
        Long orderId = orderMessage.getOrder().getId();

        try {
            secKillOrderService.asyncCreateOrder(orderMessage.getOrder(),orderMessage.getOrderItem(),orderMessage.getFlashPromotionRelationId());

            //更改排队标记状态,代表已经下单成功,ID设置为snowflake后,用ID作为状态标记
            redisStockUtil.set(RedisKeyPrefixConst.MIAOSHA_ASYNC_WAITING_PREFIX + memberId
                    + ":" + productId,orderId.toString(),60L, TimeUnit.SECONDS);

            /*
             * 设置用户购买次数,(不限制购买次数了,需要可自行放开此处,
             * 并在secKillOrderService.checkConfirm中加入验证)
             */
            /*Integer rebuy = redisOpsUtil.get(RedisKeyPrefixConst.MEMBER_BUYED_MIAOSHA_PREFIX + memberId + ":" + productId,Integer.class);

            if(rebuy != null){
                redisOpsUtil.decr(RedisKeyPrefixConst.MEMBER_BUYED_MIAOSHA_PREFIX + memberId + ":" + productId);
            }else{
                //剩余时间
                Date now = new Date();
                Long expired = endDate.getTime()-now.getTime();
                //打上购买次数标记
                redisOpsUtil.set(RedisKeyPrefixConst.MEMBER_BUYED_MIAOSHA_PREFIX + memberId + ":" + productId,limit-1
                        ,expired,TimeUnit.MILLISECONDS);
            }*/
        }
        catch (Exception e) {
            log.error("消费异步下单消息异常：",e);
            if (e instanceof DuplicateKeyException) {
                /*下单成功*/
                redisStockUtil.set(RedisKeyPrefixConst.MIAOSHA_ASYNC_WAITING_PREFIX + memberId
                        + ":" + productId, orderId.toString(),60L, TimeUnit.SECONDS);
            } else {
                /*下单失败*/
                redisStockUtil.set(RedisKeyPrefixConst.MIAOSHA_ASYNC_WAITING_PREFIX + memberId
                        + ":" + productId, Integer.toString(-1), 60L, TimeUnit.SECONDS);
            }
            secKillOrderService.failSendMessage(productId,null);
        }
    }

}
