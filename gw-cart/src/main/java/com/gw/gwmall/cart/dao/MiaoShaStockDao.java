package com.gw.gwmall.cart.dao;

import org.apache.ibatis.annotations.Param;

/**
 * 秒杀库存服务
 **/
public interface MiaoShaStockDao {

    //减库存
    Integer descStock(@Param("id") Long id, @Param("stock") Integer stock);

    /**
     * 秒杀产品乐观锁减库存
     * @param id
     *      秒杀活动库存记录ID
     * @return
     */
    Integer descStockInVersion(@Param("id") Long id, @Param("oldStock") Integer oldStock, @Param("newStock") Integer newStock);

    /**
     * 查询当前的缓存库存
     * @param id
     * @return
     */
    Integer selectMiaoShaStock(@Param("id") Long id);


    /*-------------------------悲观锁实现--------------------------*/
    /**
     * 查询加锁
     * @param id
     * @return
     */
    Integer selectMiaoShaStockInLock(@Param("id") Long id);
    /**
     *
     * @param id
     * @param stock
     * @return
     */
    Integer descStockInLock(@Param("id") Long id, @Param("stock") Integer stock);
}
