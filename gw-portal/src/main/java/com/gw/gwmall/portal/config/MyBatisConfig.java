package com.gw.gwmall.portal.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis配置类
 */
@Configuration
@EnableTransactionManagement
@MapperScan({"com.gw.gwmall.mapper","com.gw.gwmall.portal.dao"})
public class MyBatisConfig {
}
