package com.gw.gwmall.portal.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.gw.gwmall.FlashPromotionProduct;
import com.gw.gwmall.portal.domain.HomeContentResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineCacheConfig {

    @Bean(name = "promotion")
    public Cache<String, HomeContentResult> promotionCache() {
        int rnd = ThreadLocalRandom.current().nextInt(10);
        return Caffeine.newBuilder()
                // 设置最后一次写入经过固定时间过期
                .expireAfterWrite(30 + rnd, TimeUnit.MINUTES)
                // 初始的缓存空间大小
                .initialCapacity(20)
                // 缓存的最大条数
                .maximumSize(100)
                .build();
    }

    /*以双缓存的形式提升首页的访问性能，这个备份缓存其实设置为永不过期更好，
    * 可以作为首页的降级和兜底方案*/
    @Bean(name = "promotionBak")
    public Cache<String, HomeContentResult> promotionCacheBak() {
        int rnd = ThreadLocalRandom.current().nextInt(10);
        return Caffeine.newBuilder()
                // 设置最后一次访问的过期时间，备份缓存几乎不会过期
                .expireAfterAccess(41 + rnd, TimeUnit.MINUTES)
                // 初始的缓存空间大小
                .initialCapacity(20)
                // 缓存的最大条数
                .maximumSize(100)
                .build();
    }

    /*秒杀信息在首页的缓存*/
    @Bean(name = "secKill")
    public Cache<String, List<FlashPromotionProduct>> secKillCache() {
        int rnd = ThreadLocalRandom.current().nextInt(400);
        return Caffeine.newBuilder()
                // 设置最后一次写入经过固定时间过期
                .expireAfterWrite(500 + rnd, TimeUnit.MILLISECONDS)
                // 初始的缓存空间大小
                .initialCapacity(20)
                // 缓存的最大条数
                .maximumSize(100)
                .build();
    }

    /*秒杀信息在首页的缓存备份，提升首页的访问性能*/
    @Bean(name = "secKillBak")
    public Cache<String, List<FlashPromotionProduct>> secKillCacheBak() {
        int rnd = ThreadLocalRandom.current().nextInt(400);
        return Caffeine.newBuilder()
                // 设置较长过期时间
                .expireAfterWrite(2000 + rnd, TimeUnit.MILLISECONDS)
                // 初始的缓存空间大小
                .initialCapacity(20)
                // 缓存的最大条数
                .maximumSize(100)
                .build();
    }
}
