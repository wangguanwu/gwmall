package com.gw.gwmall.ordercurrent.component.rocketmq;//package com.tuling.tulingmall.component.rocketmq;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.common.api.ResultCode;
import com.gw.gwmall.ordercurrent.mapper.OmsOrderMapper;
import com.gw.gwmall.ordercurrent.model.OmsOrder;
import com.gw.gwmall.ordercurrent.service.TradeService;
import com.gw.gwmall.ordercurrent.service.impl.OrderConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;

import java.util.Date;

/**
 * 订单事务消息监听器
 * 通过事务消息机制完成订单生成。注意：这个消息只是用来发给下游服务进行订单取消用。
 */
@Slf4j
//@RocketMQTransactionListener()
public class TransactionListenerImpl implements RocketMQLocalTransactionListener {
    //由于要做微服务负载均衡，检查次数就不能在本地记录了。
//    private ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<>();
    //回查次数可以根据订单超时事件定制。普通订单超时时间来自于oms_order_settings表的normal_order_overtime表。
    //这里用5次做模拟。
    private static int maxTryMums = 5;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 下单事务消息的本地事务：
     * 1、Redis中记录回查次数
     * 2、返回UNKNOWN状态，等待事务回查
     * @param msg  orderId:memberId
     * @param arg  orderParam
     * @return
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        try{
            String transId = (String)msg.getHeaders().get(RocketMQHeaders.PREFIX + RocketMQHeaders.TRANSACTION_ID);
            log.info("------------RocketMQ执行本地订单创建 transId: "+transId+"-------------");
            /**
             * 订单已提前生成，这里就不用记录本地订单了。
             * */
            String orderId = String.valueOf(arg);
            redisTemplate.opsForHash().put(OrderConstant.REDIS_CREATE_ORDER,orderId,0);
            return RocketMQLocalTransactionState.UNKNOWN;
        }catch (Exception e){
            log.warn("-----创建RocketMQ下单事务消息失败, ---",e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    /**
     * 下单事务回查：事务状态回查最大次数transactionCheckMax 默认15次，每次回查的间隔时间transactionTimeOut 默认6秒。检查10次，模拟一分钟支付时间
     * 1、查询支付宝中的订单支付状态。--本地需要检查的状态： paytype 支付方式: 0-未支付，1-支付宝支付。 status : 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
     * 2、如果订单已支付，则返回ROLLBACK，丢弃消息 -- 支付宝订单支付后的回调接口中，会修改paytype和status，并完成订单扣减等业务逻辑。
     * 3、如果订单未支付，且超过回查次数，则修改status为5,无效订单，并返回COMMIT，推送消息 -- 下游消费者主要释放下单时锁定的库存。
     * @param msg orderId:memberId
     * @return
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        try{
            String transId = (String)msg.getHeaders().get(RocketMQHeaders.PREFIX + RocketMQHeaders.TRANSACTION_ID);
            log.info("------------RocketMQ回查订单状态 transId: "+transId+"-------------");
            String message = new String((byte[]) msg.getPayload());
            if(StringUtils.isNotEmpty(message) && message.contains(":") ){
                String[] split = message.split(":");
                String orderId = split[0];
//                Long memberId = Long.parseLong(split[1]);
                Object redisRecord = redisTemplate.opsForHash().get(OrderConstant.REDIS_CREATE_ORDER, orderId);
                if(null == redisRecord){
                    log.warn("-----RocketMQ订单事务回查失败, 没有Redis记录");
                    return RocketMQLocalTransactionState.ROLLBACK;
                }
                int retryTimes = (int)redisRecord;
                //1、超过最大检查次数，表示订单超时未支付，关闭订单
                if(retryTimes >= maxTryMums){
                    OmsOrder order = new OmsOrder();
                    order.setId(Long.parseLong(orderId));
                    order.setStatus(5); //订单状态修改为关闭
//                order.setPayType(0); //支付状态修改为未支付
                    order.setPaymentTime(new Date());
                    orderMapper.updateByPrimaryKeySelective(order);
                    redisTemplate.opsForHash().delete(OrderConstant.REDIS_CREATE_ORDER,orderId);
                    log.info("--- 订单下单事务消息 transID: "+transId+"支付超时，发送消息，释放锁定的库存");
                    return RocketMQLocalTransactionState.COMMIT;
                }else{
                    //2、查询支付宝订单支付状态
                    //2.1 如果支付宝支付成功，这个方法中会更新订单paytype和status,并完成扣减库存
                    CommonResult commonResult = tradeService.tradeStatusQuery(Long.parseLong(orderId), 1);
                    if(ResultCode.SUCCESS.getCode() == commonResult.getCode()){
                        msg.getHeaders().remove("CHECK_TIME");
//                    localTrans.remove(transId);
                        log.info("--- 订单下单事务消息 transID: "+transId+";订单号："+orderId+"已经完成支付，回滚消息");
                        return RocketMQLocalTransactionState.ROLLBACK;
                    }else{
                        log.info("--- 订单下单事务消息 transID: "+transId+";订单号："+orderId+"未支付，已回查"+retryTimes+"次，等待下次回查");
                        //2.2 如果支付宝未支付，记录回查次数，等待下次回查。
                        redisTemplate.opsForHash().increment(OrderConstant.REDIS_CREATE_ORDER,orderId,1);
                        //消息的header不可修改。可以思考下为什么？
//                msg.getHeaders().put("CHECK_TIME",retryTimes+1);
                        return RocketMQLocalTransactionState.UNKNOWN;
                    }
                }
            }else{
                log.info("----RocketMQ订单消息格式不对，丢弃消息。");
                return RocketMQLocalTransactionState.ROLLBACK;
            }
        }catch (Exception e){
            log.warn("-----RcoketMQ下单事务消息状态回查失败, ---",e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }
}