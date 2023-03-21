package com.gw.gwmall.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EsProductResponse {

    private String name;
    private String keywords;
    private String subTitle;
    private BigDecimal price;
    private BigDecimal promotionPrice;
    private BigDecimal originalPrice;
    private String pic;
    private Integer sale;
    private Long brandId;
    private String brandName;
    private Long categoryId;
    private String categoryName;
    private Long id;

}
