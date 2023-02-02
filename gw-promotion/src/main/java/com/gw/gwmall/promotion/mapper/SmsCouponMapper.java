package com.gw.gwmall.promotion.mapper;

import com.gw.gwmall.promotion.model.SmsCoupon;
import com.gw.gwmall.promotion.model.SmsCouponExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SmsCouponMapper {
    long countByExample(SmsCouponExample example);

    int deleteByExample(SmsCouponExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SmsCoupon record);

    int insertSelective(SmsCoupon record);

    List<SmsCoupon> selectByExample(SmsCouponExample example);

    SmsCoupon selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SmsCoupon record, @Param("example") SmsCouponExample example);

    int updateByExample(@Param("record") SmsCoupon record, @Param("example") SmsCouponExample example);

    int updateByPrimaryKeySelective(SmsCoupon record);

    int updateByPrimaryKey(SmsCoupon record);

    int updateCountAndReceiveCountByPrimaryKey(Long id);
}