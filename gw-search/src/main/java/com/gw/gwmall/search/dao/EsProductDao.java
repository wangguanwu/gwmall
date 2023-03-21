package com.gw.gwmall.search.dao;

import com.gw.gwmall.search.domain.EsProduct;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 搜索系统中的商品管理自定义Dao
 */
public interface EsProductDao {
    List<EsProduct> getAllEsProductList(@Param("id") Long id);

    List<EsProduct> getAllProductList();

    /**
     * 查询给定商品id列表，
     * @param idList
     * @return
     */
    List<EsProduct> getProductList(@Param("productIdList") Iterable<Long> idList);

}
