package com.gw.gwmall.ordercurrent.domain;

import lombok.Data;

/**
* @desc: 类的描述:mq取消订单封装对象
*/
@Data
public class MqCancelOrder {

    private Long orderId;

    private Long memberId;
}
