package com.gw.gwmall.cart.config;

import com.gw.gwmall.cart.feignapi.interceptor.HeaderInterceptor;
import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author guanwu
 * @created on 2023-02-04 22:15:55
 **/

@Configuration
public class FeignConfig {

    @Bean
    public Logger.Level level() {
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new HeaderInterceptor();
    }
}
