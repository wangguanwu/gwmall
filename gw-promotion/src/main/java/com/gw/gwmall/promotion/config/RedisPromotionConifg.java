package com.gw.gwmall.promotion.config;

import com.gw.gwmall.promotion.util.RedisDistrLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisPromotionConifg {

    @Bean
    public RedisDistrLock redisDistrLock(){
        return new RedisDistrLock();
    }

}
