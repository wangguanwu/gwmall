package com.gw.mall.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class SystemConf {
    private Logger logger = LoggerFactory.getLogger(SystemConf.class);

    @Value("${spring.cloud.nacos.discovery.server-addr}")
    private String nacosDiscoveryAddr;

    @PostConstruct
    public void showConf(){
        logger.info("启动配置 nacosDiscoveryAddr:[{}]",nacosDiscoveryAddr);
    }

}
