package com.gw.gwmall.cart.service.impl;

import com.gw.gwmall.cart.feignapi.unqid.UnqidFeignApi;
import lombok.extern.slf4j.Slf4j;

import java.util.AbstractQueue;
import java.util.List;

import static com.gw.gwmall.cart.service.impl.SecKillConfirmOrderServiceImpl.FETCH_PERIOD;
import static com.gw.gwmall.cart.service.impl.SecKillConfirmOrderServiceImpl.ORDER_COUNT_LIMIT_SECOND;

@Slf4j
public class RefreshIdListTask implements Runnable{

    public static final String LEAF_ORDER_ID_KEY = "order_id";
    public static final String LEAF_ORDER_ITEM_ID_KEY = "order_item_id";

    private final AbstractQueue<String> orderIdQueue;
    private final AbstractQueue<String> orderItemIdQueue;
    private final UnqidFeignApi unqidFeignApi;

    public RefreshIdListTask(AbstractQueue<String> orderIdQueue,
                             UnqidFeignApi unqidFeignApi,
                             AbstractQueue<String> orderItemIdQueue) {
        this.orderIdQueue = orderIdQueue;
        this.unqidFeignApi = unqidFeignApi;
        this.orderItemIdQueue = orderItemIdQueue;
    }

    @Override
    public void run() {
        if (!orderIdQueue.isEmpty()) return;
        try {
            int getCount = ORDER_COUNT_LIMIT_SECOND / (1000 / FETCH_PERIOD);
            List<String> segmentIdList = unqidFeignApi.getSegmentIdList(LEAF_ORDER_ID_KEY, getCount);
            orderIdQueue.addAll(segmentIdList);
            log.info("成功刷新订单id列表，个数{}",segmentIdList.size());
            List<String> segmentItemIdList = unqidFeignApi.getSegmentIdList(LEAF_ORDER_ITEM_ID_KEY,
                    ORDER_COUNT_LIMIT_SECOND);
            orderItemIdQueue.addAll(segmentItemIdList);
            log.info("成功刷新订单详情id列表，个数{}",segmentIdList.size());
        } catch (Exception e) {
            log.error("获取订单id列表异常：",e);
        }
    }
}
