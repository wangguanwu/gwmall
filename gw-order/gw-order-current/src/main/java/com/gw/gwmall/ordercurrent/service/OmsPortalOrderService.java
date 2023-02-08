package com.gw.gwmall.ordercurrent.service;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.ordercurrent.domain.MqCancelOrder;
import com.gw.gwmall.ordercurrent.domain.OmsOrderDetail;
import com.gw.gwmall.ordercurrent.domain.OrderParam;
import org.apache.shardingsphere.transaction.annotation.ShardingTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 前台订单管理Service
 */
public interface OmsPortalOrderService {
//    /**
//     * 根据用户购物车信息生成确认单信息
//     */
//    ConfirmOrderResult generateConfirmOrder(List<Long> itemIds,Long memberId) throws BusinessException;

    /**
     * 根据提交信息生成订单
     */
    @Transactional
    @ShardingTransactionType(TransactionType.XA)
    CommonResult generateOrder(OrderParam orderParam,Long memberId);

    /**
     * 生成订单的orderId
     * @param memberId 用户ID
     */
    public Long generateOrderId(Long memberId);
    /**
     * 订单详情
     * @param orderId
     * @return
     */
    CommonResult getDetailOrder(Long orderId);

    /**
     * 支付成功后的回调
     */
    @Transactional
    Integer paySuccess(Long orderId, Integer payType);

    /**
     * 自动取消超时订单
     */
    @Transactional
    CommonResult cancelTimeOutOrder();

    /**
     * 取消单个超时订单
     */
    @Transactional
    void cancelOrder(Long orderId,Long memberId);

    /**
     * 删除订单[逻辑删除],只能status为：3->已完成；4->已关闭；5->无效订单，才可以删除
     * ，否则只能先取消订单然后删除。
     * @param orderId
     * @return
     *      受影响的行
     */
    @Transactional
    int deleteOrder(Long orderId);
    /**
     * 发送延迟消息取消订单
     */
    void sendDelayMessageCancelOrder(MqCancelOrder mqCancelOrder);

    /**
     * 查询会员的订单
     * @param pageSize
     * @param pageNum
     * @param memberId
     *      会员ID
     * @param status
     *      订单状态
     * @return
     */
    CommonResult<List<OmsOrderDetail>> findMemberOrderList(Integer pageSize, Integer pageNum, Long memberId, Integer status);
}
