package com.gw.gwmall;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author guanwu
 */

@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@EnableRedisHttpSession// 开启spring session
public class GwMallMemberApplication {
    public static void main(String[] args) {
        SpringApplication.run(GwMallMemberApplication.class, args);
    }
}
