package com.gw.gwmall;

import com.gw.gwmall.model.PmsProduct;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀信息和商品对象封装
 */
@Getter
@Setter
public class FlashPromotionProduct extends PmsProduct {
    private Long relationId;
    private Long flashPromotionId;
    private BigDecimal flashPromotionPrice;
    private Integer flashPromotionCount;
    private Integer flashPromotionLimit;
    private Date flashPromotionStartDate;
    private Date flashPromotionEndDate;
    private String secKillServer;
}
