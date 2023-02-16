package com.gw.gwmall;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author guanwu
 * @created on 2023-02-08 20:54:55
 **/

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
public class GwMallOrderHistoryApplication {
    public static void main(String[] args) {
        SpringApplication.run(GwMallOrderHistoryApplication.class, args);
    }

}
