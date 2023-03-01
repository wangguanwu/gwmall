package com.gw.gwmall.portal.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class PromotionRedisKey {

    @Value ("${namespace.promotion:prmtd}")
    private String promotionNamespace;

    @Value ("${promotion.brand:br}")
    private String brand;

    @Value ("${promotion.newProduct:np}")
    private String newProduct;

    @Value ("${promotion.recProduct:rp}")
    private String recProduct;

    @Value ("${promotion.homeAdvertise:hd}")
    private String homeAdvertise;

    @Value ("${promotion.seckill:sk}")
    private String secKill;

    private String brandKey;
    private String newProductKey;
    private String recProductKey;
    private String homeAdvertiseKey;
    private String secKillKey;

    @PostConstruct
    public void initKey(){
        brandKey = promotionNamespace + "." + brand;
        newProductKey = promotionNamespace + "." + newProduct;
        recProductKey = promotionNamespace + "." + recProduct;
        homeAdvertiseKey = promotionNamespace + "." + homeAdvertise;
        secKillKey = promotionNamespace + "." + secKill;
        StringBuilder logKeyStr = new StringBuilder();
        logKeyStr.append("[品牌推荐redis主键=").append(brandKey)
                .append("] [新品推荐redis主键=").append(newProductKey)
                .append("] [人气推荐redis主键=").append(recProductKey)
                .append("] [轮播广告redis主键=").append(homeAdvertiseKey)
                .append("] [秒杀redis主键=").append(secKillKey)
                .append("]");
        log.info("促销系统Redis主键配置：{}",logKeyStr);
    }

    public String getBrandKey() {
        return brandKey;
    }

    public String getNewProductKey() {
        return newProductKey;
    }

    public String getRecProductKey() {
        return recProductKey;
    }

    public String getHomeAdvertiseKey() {
        return homeAdvertiseKey;
    }

    public String getSecKillKey() {
        return secKillKey;
    }

    @Value("${promotion.demo.allowLocalCache:true}")
    private boolean allowLocalCache;

    @Value("${promotion.demo.allowRemoteCache:true}")
    private boolean allowRemoteCache;

    public boolean isAllowLocalCache() {
        return allowLocalCache;
    }

    public boolean isAllowRemoteCache() {
        return allowRemoteCache;
    }
}
