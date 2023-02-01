package com.gw.gwmall.config;

import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 */
@Configuration
public class RibbonConfig {

    /**
     * 方法实现说明:原生的RestTemplate +@LB不行 因为网关服务启动时，需要实现InitializingBean接口调用token_key获取jwt公钥，
     * InitializingBean方法执行前RestTemplate还没有被LoadBalancerInterceptor增强，会报错，
     * 因此需要手动注入loadBalancerInterceptor拦截器，实现负载均衡功能
     *
     */
    @Bean
    public RestTemplate restTemplate(LoadBalancerInterceptor loadBalancerInterceptor){
        RestTemplate restTemplate = new RestTemplate();
        List<ClientHttpRequestInterceptor> list = new ArrayList<>();
        list.add(loadBalancerInterceptor);
        restTemplate.setInterceptors(list);
        return restTemplate;
    }

}
