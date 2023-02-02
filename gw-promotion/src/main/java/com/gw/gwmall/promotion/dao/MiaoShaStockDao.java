package com.gw.gwmall.promotion.dao;

import org.apache.ibatis.annotations.Param;

/**
 * @description: 秒杀库存服务
 **/
public interface MiaoShaStockDao {

    //减库存
    Integer descStock(@Param("id") Long id, @Param("stock") Integer stock);

    Integer incStock(@Param("id") Long id, @Param("stock") Integer stock);

    Integer getStock(@Param("id") Long id);

}
