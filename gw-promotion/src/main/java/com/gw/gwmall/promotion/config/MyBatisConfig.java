package com.gw.gwmall.promotion.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis配置类
 */
@Configuration
@EnableTransactionManagement
@MapperScan({"com.gw.gwmall.promotion.mapper","com.gw.gwmall.promotion.dao"})
public class MyBatisConfig {
}
