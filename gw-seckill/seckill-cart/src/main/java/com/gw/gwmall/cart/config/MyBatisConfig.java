package com.gw.gwmall.cart.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis配置类
 */
@Configuration
@EnableTransactionManagement
@MapperScan({"com.gw.gwmall.cart.mapper","com.gw.gwmall.cart.dao"})
public class MyBatisConfig {

}
