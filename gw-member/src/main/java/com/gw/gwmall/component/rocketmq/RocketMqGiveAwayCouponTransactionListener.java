package com.gw.gwmall.component.rocketmq;

import com.gw.gwmall.common.domain.UserCoupon;
import com.gw.gwmall.common.util.JacksonUtils;
import com.gw.gwmall.mapper.UmsMemberMapper;
import com.gw.gwmall.model.UmsMember;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author guanwu
 * @created on 2023-02-14 17:30:35
 *
 * 赠送用户优惠券
 **/


@Slf4j
@Component
@RocketMQTransactionListener
public class RocketMqGiveAwayCouponTransactionListener implements RocketMQLocalTransactionListener {

    @Resource
    private UmsMemberMapper umsMemberMapper;
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        log.info("购物券事务消息,local transaction...");
        UmsMember umsMember = queryUser(msg);
        if (null == umsMember) {
            return RocketMQLocalTransactionState.UNKNOWN;
        }
        return RocketMQLocalTransactionState.COMMIT;
    }

    private UmsMember queryUser(Message<?> msg) {
        Object payload = msg.getPayload();

        String msgStr = new String((byte[]) payload);
        UserCoupon userCoupon = JacksonUtils.toObj(msgStr, UserCoupon.class);
        return umsMemberMapper.selectByPrimaryKey(userCoupon.getMemberId());
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        UmsMember umsMember = queryUser(msg);
        if (umsMember != null) {
            return RocketMQLocalTransactionState.COMMIT;
        }

        log.info("购物券事务消息回查,local transaction...");
        return RocketMQLocalTransactionState.COMMIT;
    }
}
