package com.gw.gwmall.history.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 */
@Configuration
@MapperScan({"com.gw.gwmall.history.mapper","com.gw.gwmall.history.dao"})
public class MyBatisConfig {


}
