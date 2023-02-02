package com.gw.gwmall.promotion.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
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

    @Value ("${promotion.distlock.brand:dlbr}")
    private String dlBrand;

    @Value ("${promotion.distlock.newProduct:dlnp}")
    private String dlNewProduct;

    @Value ("${promotion.distlock.recProduct:dlrp}")
    private String dlRecProduct;

    @Value ("${promotion.distlock.homeAdvertise:dlhd}")
    private String dlHomeAdvertise;

    @Value ("${promotion.distlock.timeOut:5000}")
    private long dlTimeout;

    private String dlBrandKey;
    private String dlNewProductKey;
    private String dlRecProductKey;
    private String dlHomeAdvertiseKey;

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
        dlBrandKey = promotionNamespace + "." + dlBrand;
        dlNewProductKey = promotionNamespace + "." + dlNewProduct;
        dlRecProductKey = promotionNamespace + "." + dlRecProduct;
        dlHomeAdvertiseKey = promotionNamespace + "." + dlHomeAdvertise;
        StringBuilder logKeyStr = new StringBuilder();
        logKeyStr.append("[品牌推荐redis主键=").append(brandKey)
                .append("] [新品推荐redis主键=").append(newProductKey)
                .append("] [人气推荐redis主键=").append(recProductKey)
                .append("] [轮播广告redis主键=").append(homeAdvertiseKey)
                .append("] [秒杀redis主键=").append(secKillKey)
                .append("] [品牌推荐redis分布式锁主键=").append(dlBrandKey)
                .append("] [新品推荐redis分布式锁主键=").append(dlNewProductKey)
                .append("] [人气推荐redis分布式锁主键=").append(dlRecProductKey)
                .append("] [轮播广告redis分布式锁主键=").append(dlHomeAdvertiseKey)
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

    public String getDlBrandKey() {
        return dlBrandKey;
    }

    public String getDlNewProductKey() {
        return dlNewProductKey;
    }

    public String getDlRecProductKey() {
        return dlRecProductKey;
    }

    public String getDlHomeAdvertiseKey() {
        return dlHomeAdvertiseKey;
    }

    public long getDlTimeout() {
        return dlTimeout;
    }
}
