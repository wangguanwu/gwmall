package com.gw.gwmall.portal.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.gw.gwmall.portal.config.PromotionRedisKey;
import com.gw.gwmall.portal.domain.HomeContentResult;
import com.gw.gwmall.portal.service.HomeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache")
@Api(tags = "CacheManagerController", description = "本地缓存管理")
public class CacheManagerController {

    @Autowired
    private PromotionRedisKey promotionRedisKey;

    @Autowired
    @Qualifier("promotion")
    private Cache<String, HomeContentResult> promotionCache;

    @Autowired
    @Qualifier("promotionBak")
    private Cache<String, HomeContentResult> promotionCacheBak;

    @Autowired
    private HomeService homeService;

    @ApiOperation("强制本地缓存失效")
    @RequestMapping(value = "/invalid", method = RequestMethod.GET)
    public String invalidCache(@RequestParam(value = "cacheType") int cacheType) {
        final String brandKey = promotionRedisKey.getBrandKey();
        if(0 == cacheType) promotionCache.invalidateAll();
        if(1 == cacheType) promotionCacheBak.invalidateAll();
        if(2 == cacheType) {
            promotionCacheBak.invalidateAll();
            promotionCache.invalidateAll();
        }
        return "强制本地缓存失效完成";
    }

    @ApiOperation("刷新本地缓存")
    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    public String refreshCache(@RequestParam(value = "cacheType") int cacheType) {
        final String brandKey = promotionRedisKey.getBrandKey();
        HomeContentResult result = homeService.getFromRemote();
        if(null != result){
            if(0 == cacheType) promotionCache.put(brandKey,result);
            if(1 == cacheType) promotionCacheBak.put(brandKey,result);
            if(2 == cacheType) {
                promotionCacheBak.put(brandKey,result);
                promotionCache.put(brandKey,result);
            }
            return "刷新本地缓存完成";
        }else{
            return "从远程服务未获得数据，刷新本地缓存失败";
        }
    }

}
