package com.gw.gwmall.promotion.dao;

import com.gw.gwmall.promotion.domain.FlashPromotionParam;
import org.apache.ibatis.annotations.Param;

/**
 * 首页秒杀活动内容管理自定义Dao
 */
public interface FlashPromotionProductDao {

    /**
     * 查找所有的秒杀活动商品
     * @return
     */
    FlashPromotionParam getFlashPromotion(@Param("spid") Long spid, @Param("status") Integer status);
}
