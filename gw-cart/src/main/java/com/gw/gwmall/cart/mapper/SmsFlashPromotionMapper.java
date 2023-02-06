package com.gw.gwmall.cart.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.gw.gwmall.cart.model.SmsFlashPromotion;
import com.gw.gwmall.cart.model.SmsFlashPromotionExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@DS("promotion")
public interface SmsFlashPromotionMapper {
    long countByExample(SmsFlashPromotionExample example);

    int deleteByExample(SmsFlashPromotionExample example);

    int deleteByPrimaryKey(Long id);

    int insert(SmsFlashPromotion record);

    int insertSelective(SmsFlashPromotion record);

    List<SmsFlashPromotion> selectByExample(SmsFlashPromotionExample example);

    SmsFlashPromotion selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SmsFlashPromotion record, @Param("example") SmsFlashPromotionExample example);

    int updateByExample(@Param("record") SmsFlashPromotion record, @Param("example") SmsFlashPromotionExample example);

    int updateByPrimaryKeySelective(SmsFlashPromotion record);

    int updateByPrimaryKey(SmsFlashPromotion record);
}