package com.gw.gwmall.rediscomm.config;

import com.gw.gwmall.rediscomm.util.RedisOpsExtUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author guanwu
 * @created on 2023-03-26 17:39:01
 **/

@Configuration
public class RedisConfig {

    @Bean
    @ConditionalOnMissingBean(RedisOpsExtUtil.class)
    public RedisOpsExtUtil redisOpsUtil(){
        return new RedisOpsExtUtil();
    }


}
