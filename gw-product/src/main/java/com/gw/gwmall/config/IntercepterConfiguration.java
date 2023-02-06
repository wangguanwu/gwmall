package com.gw.gwmall.config;

import com.gw.gwmall.intercepter.BloomFilterInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author guanwu
 **/
@Configuration
public class IntercepterConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册拦截器
        registry.addInterceptor(authInterceptorHandler())
                .addPathPatterns("/pms/productInfo/**");
    }

    @Bean
    public BloomFilterInterceptor authInterceptorHandler(){
        return new BloomFilterInterceptor();
    }
}
