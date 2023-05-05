package com.gw.gwmall.config;

import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;
import com.gw.gwmall.component.BloomFilterService;
import com.gw.gwmall.component.BloomRedisService;
import com.gw.gwmall.component.BloomRedisServiceOpt;
import com.gw.gwmall.util.BloomFilterHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 布隆过滤器配置
 **/
@Slf4j
@Configuration
public class BloomFilterConfig{



    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Bean
    public BloomFilterHelper<String> initBloomFilterHelper() {
        return new BloomFilterHelper<>((Funnel<String>) (from, into) -> into.putString(from, Charsets.UTF_8)
                .putString(from, Charsets.UTF_8), 1000000, 0.01);
    }

    /**
     * 布隆过滤器bean注入
     * @return
     */

    @ConditionalOnProperty(prefix = "bloomfilter.config", name = "type", havingValue = "guava", matchIfMissing = true)
    @Bean
    public BloomFilterService guavaBloomRedisService(){
        BloomRedisService bloomRedisService = new BloomRedisService();
        bloomRedisService.setBloomFilterHelper(initBloomFilterHelper());
        bloomRedisService.setRedisTemplate(redisTemplate);
        return bloomRedisService;
    }

    @ConditionalOnProperty(prefix = "bloomfilter.config", name = "type", havingValue = "redis-plugin")
    @Bean
    public BloomFilterService redisPluginBloomRedisService(RedisTemplate<String, String> strRedisTemplate){
        return new BloomRedisServiceOpt(strRedisTemplate);
    }
}
