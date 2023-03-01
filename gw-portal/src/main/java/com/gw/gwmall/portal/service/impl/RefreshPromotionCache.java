package com.gw.gwmall.portal.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.gw.gwmall.FlashPromotionProduct;
import com.gw.gwmall.portal.config.PromotionRedisKey;
import com.gw.gwmall.portal.domain.HomeContentResult;
import com.gw.gwmall.portal.service.HomeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * 异步刷新促销信息在本地的缓存*/
@Service
@Slf4j
public class RefreshPromotionCache {

    @Autowired
    private HomeService homeService;

    @Autowired
    @Qualifier("promotion")
    private Cache<String, HomeContentResult> promotionCache;

    @Autowired
    @Qualifier("promotionBak")
    private Cache<String, HomeContentResult> promotionCacheBak;

    @Autowired
    @Qualifier("secKill")
    private Cache<String, List<FlashPromotionProduct>> secKillCache;

    @Autowired
    @Qualifier("secKillBak")
    private Cache<String, List<FlashPromotionProduct>> secKillCacheBak;

    @Autowired
    private PromotionRedisKey promotionRedisKey;

    @Async
    @Scheduled(initialDelay = 5000 * 60, fixedDelay = 1000 * 60)
    public void refreshCache() {
        if (!promotionRedisKey.isAllowLocalCache()) {
            log.info("不刷新本地缓存");
            return;
        }
        log.info("检查本地缓存[promotionCache] 是否需要刷新...");
        final String brandKey = promotionRedisKey.getBrandKey();
        if (!shouldRefreshPromotionCache(brandKey)) {
            log.warn("不刷新本地缓存");
            return;
        }
        HomeContentResult result = homeService.getFromRemote();
        if (null == result) {
            log.warn("从远程获得[promotionCache] 数据失败");
            return;
        }
        if (null == promotionCache.getIfPresent(brandKey)) {
            promotionCache.put(brandKey, result);
            log.info("刷新本地缓存[promotionCache] 成功");
        }
        if (null == promotionCacheBak.getIfPresent(brandKey)) {
            promotionCacheBak.put(brandKey, result);
            log.info("刷新本地缓存[promotionCacheBak] 成功");
        }
    }

    private boolean shouldRefreshPromotionCache(String brandKey) {
        return null == promotionCache.getIfPresent(brandKey) ||
                null == promotionCacheBak.getIfPresent(brandKey);
    }

    @Async
    @Scheduled(initialDelay = 30, fixedDelay = 30)
    public void refreshSecKillCache() {
        final String secKillKey = promotionRedisKey.getSecKillKey();
        if (!shouldRefreshSecKillCache(secKillKey)) {
            return;
        }
        List<FlashPromotionProduct> secKills = homeService.getSecKillFromRemote();
        if (null == secKills) {
            log.warn("从远程获得[SecKillCache] 数据失败");
            return;
        }
        if (null == secKillCache.getIfPresent(secKillKey)) {
            secKillCache.put(secKillKey, secKills);
        }
        if (null == secKillCacheBak.getIfPresent(secKillKey)) {
            secKillCacheBak.put(secKillKey, secKills);
        }
    }


    private boolean shouldRefreshSecKillCache(String brandKey) {
        return null == secKillCache.getIfPresent(brandKey) ||
                null == secKillCacheBak.getIfPresent(brandKey);
    }

}
