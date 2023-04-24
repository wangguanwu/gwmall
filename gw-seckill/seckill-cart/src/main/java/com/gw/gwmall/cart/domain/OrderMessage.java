package com.gw.gwmall.cart.domain;

import com.gw.gwmall.cart.model.OmsOrder;
import com.gw.gwmall.cart.model.OmsOrderItem;
import lombok.Data;

import java.util.Date;

/**
 * @author guanwu
 **/
@Data
public class OrderMessage {

    private OmsOrder order;

    private OmsOrderItem orderItem;

    //秒杀活动记录ID
    private Long flashPromotionRelationId;

    //限购数量
    private Integer flashPromotionLimit;
    /*
     * 秒杀活动结束日期
     */
    private Date flashPromotionEndDate;
}
