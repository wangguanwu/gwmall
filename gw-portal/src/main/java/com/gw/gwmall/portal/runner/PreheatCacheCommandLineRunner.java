package com.gw.gwmall.portal.runner;

import com.gw.gwmall.portal.service.HomeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class PreheatCacheCommandLineRunner implements CommandLineRunner {

    @Autowired
    private HomeService homeService;

    @Override
    public void run(String... args) {
        for(String str : args) {
            log.info("系统启动命令行参数: {}",str);
        }
        homeService.preheatCache();
    }

}
