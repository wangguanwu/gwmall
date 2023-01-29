package com.gw.gwmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author guanwu
 */

@SpringBootApplication
@EnableRedisHttpSession// 开启spring session
public class GeMallMemberApplication {
    public static void main(String[] args) {
        SpringApplication.run(GeMallMemberApplication.class, args);
    }
}
