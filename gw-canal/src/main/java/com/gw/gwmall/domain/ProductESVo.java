package com.gw.gwmall.domain;

import lombok.Data;

import java.math.BigDecimal;

/*往ES存取时的数据实体类*/
@Data
public class ProductESVo {

    private String name;
    private String keywords;
    private String subTitle;
    private BigDecimal price;
    private BigDecimal promotionPrice;
    private BigDecimal originalPrice;
    private String pic;
    private Integer saleCount;
    private Long brandId;
    private String brandName;
    private Long categoryId;
    private String categoryName;

}
