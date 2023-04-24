package com.gw.gwmall.ordercurrent.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 */
@Configuration
@MapperScan({"com.gw.gwmall.ordercurrent.mapper","com.gw.gwmall.ordercurrent.dao"})
public class MyBatisConfig {

}
