package com.gw.gwmall.promotion.service;

import com.gw.gwmall.promotion.service.impl.ConstantPromotion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/*系统启动时进行缓存预热*/
@Component
@Slf4j
public class preheatCache implements CommandLineRunner {

    @Autowired
    private HomePromotionService homePromotionService;

    @Override
    public void run(String... args) throws Exception {
        for(String str : args) {
            log.info("系统启动命令行参数: {}",str);
        }
        homePromotionService.content(ConstantPromotion.HOME_GET_TYPE_ALL);
    }
}
