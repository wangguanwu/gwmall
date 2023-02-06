package com.gw.gwmall.config;

import com.gw.gwmall.dao.FlashPromotionProductDao;
import com.gw.gwmall.rediscomm.util.RedisOpsExtUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @description: 将秒杀活动信息load到redis，暂时不用#{yangguo}
 **/
@Configuration
public class LoadSeckillProduct2Cache implements InitializingBean {

    @Autowired
    private FlashPromotionProductDao flashPromotionProductDao;

    @Autowired
    private RedisOpsExtUtil redisOpsUtil;

//    /**
//     * 将秒杀活动信息load到redis，暂时不用#{yangguo}
//     */
//    public void loadFlashPromotion2Cache(){
//
//        FlashPromotionParam promotion = flashPromotionProductDao.getFlashPromotion(null);
//        if (null==promotion){
//            return;
//        }
//        Date now = new Date();
//        Date endDate = promotion.getEndDate();//结束时间
//        final Long expired = endDate.getTime()-now.getTime();//剩余时间
//        //秒杀商品库存缓存到redis
//        promotion.getRelation().stream().forEach((item)->{
//            redisOpsUtil.setIfAbsent(
//                    RedisKeyPrefixConst.MIAOSHA_STOCK_CACHE_PREFIX + item.getProductId()
//                    , item.getFlashPromotionCount()
//                    , expired
//                    , TimeUnit.MILLISECONDS);
//        });

//        /*
//         * 将秒杀商品读取到缓存
//         */
//        if(!redisTemplate.hasKey(RedisKeyPrefixConst.FLASH_PROMOTION_PRODUCT_KEY)){
//            //从dbload出所有在进行当中的秒杀产品
//            FlashPromotionParam promotion = dao.getFlashPromotion(null);
//            if(!ObjectUtils.isEmpty(promotion)){
//                //将产品缓存到redis-hash中
//                promotion.getRelation().stream().forEach(item->{
//                    redisTemplate.opsForHash().put(RedisKeyPrefixConst.FLASH_PROMOTION_PRODUCT_KEY,item.getProductId(),item);
//                });
//
//                SmsFlashPromotion smsFlashPromotion = new SmsFlashPromotion();
//                BeanUtils.copyProperties(promotion,smsFlashPromotion);
//
//                redisTemplate.opsForHash().put(RedisKeyPrefixConst.FLASH_PROMOTION_PRODUCT_KEY,
//                        RedisKeyPrefixConst.ACTIVE_FLASH_PROMOTION_KEY, smsFlashPromotion);
//
//                Date now = new Date();
//                Long timeout = promotion.getEndDate().getTime() - now.getTime();
//                //设置key过期时间
//                redisTemplate.expire(RedisKeyPrefixConst.FLASH_PROMOTION_PRODUCT_KEY,timeout, TimeUnit.MILLISECONDS);
//            }
//        }
//    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //loadFlashPromotion2Cache();
    }
}
