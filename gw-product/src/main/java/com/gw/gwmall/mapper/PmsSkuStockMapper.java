package com.gw.gwmall.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.gw.gwmall.domain.StockChanges;
import com.gw.gwmall.model.PmsSkuStock;
import com.gw.gwmall.model.PmsSkuStockExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@DS("goods")
public interface PmsSkuStockMapper {
    long countByExample(PmsSkuStockExample example);

    int deleteByExample(PmsSkuStockExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PmsSkuStock record);

    int insertSelective(PmsSkuStock record);

    List<PmsSkuStock> selectByExample(PmsSkuStockExample example);

    PmsSkuStock selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") PmsSkuStock record, @Param("example") PmsSkuStockExample example);

    int lockStockByExample(@Param("lockQuantity") Integer lockQuantity, @Param("example") PmsSkuStockExample example);

    int reduceStockByExample(@Param("reduceQuantity") Integer reduceQuantity, @Param("example") PmsSkuStockExample example);

    int updateSkuStock(@Param("itemList") List<StockChanges> orderItemList);

    int recoverStockByExample(@Param("recoverQuantity") Integer recoverQuantity, @Param("example") PmsSkuStockExample example);

    int updateByExample(@Param("record") PmsSkuStock record, @Param("example") PmsSkuStockExample example);

    int updateByPrimaryKeySelective(PmsSkuStock record);

    int updateByPrimaryKey(PmsSkuStock record);
}