package com.gw.gwmall.filter;

import com.alibaba.fastjson.JSON;
import com.gw.gwmall.common.api.ResultCode;
import com.gw.gwmall.common.exception.GateWayException;
import com.gw.gwmall.properties.NotAuthUrlProperties;
import com.gw.gwmall.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.PublicKey;
import java.util.Map;

/**
 * 认证过滤器,根据url判断用户请求是要经过认证 才能访问
 */
@Component
@Slf4j
@EnableConfigurationProperties(value = NotAuthUrlProperties.class)
public class AuthorizationFilter implements GlobalFilter,Ordered,InitializingBean {


    @Autowired
    private RestTemplate restTemplate;

    /**
     * 请求各个微服务 不需要用户认证的URL
     */
    @Autowired
    private NotAuthUrlProperties notAuthUrlProperties;

    /**
     * jwt的公钥,需要网关启动,远程调用认证中心去获取公钥
     */
    private PublicKey publicKey;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String currentUrl = exchange.getRequest().getURI().getPath();

        //1:不需要认证的url
        if(shouldSkip(currentUrl)) {
            //log.info("跳过认证的URL:{}",currentUrl);
            return chain.filter(exchange);
        }

        //log.info("需要认证的URL:{}",currentUrl);

        //第一步:解析出我们Authorization的请求头  value为: “bearer XXXXXXXXXXXXXX”
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        //第二步:判断Authorization的请求头是否为空
        if(StringUtils.isEmpty(authHeader)) {
            log.warn("需要认证的url,请求头为空");
            throw new GateWayException(ResultCode.AUTHORIZATION_HEADER_IS_EMPTY);
        }

        //第三步 校验jwt 若jwt不对或者超时都会抛出异常
        Claims claims = JwtUtils.validateJwtToken(authHeader,publicKey);

        //第四步 把从jwt中解析出来的 用户登陆信息存储到请求头中
        ServerWebExchange webExchange = wrapHeader(exchange,claims);

        return chain.filter(webExchange);

    }

    /**
     * 方法实现说明:把我们从jwt解析出来的用户信息存储到请求中
     */
    private ServerWebExchange wrapHeader(ServerWebExchange serverWebExchange,Claims claims) {

        String loginUserInfo = JSON.toJSONString(claims);

        log.info("jwt的用户信息:{}",loginUserInfo);

        String memberId = claims.get("additionalInfo",Map.class).get("memberId").toString();

        String nickName = claims.get("additionalInfo",Map.class).get("nickName").toString();

        //向headers中放文件，记得build
        ServerHttpRequest request = serverWebExchange.getRequest().mutate()
                .header("username",claims.get("user_name",String.class))
                .header("memberId",memberId)
                .header("nickName",nickName)
                .build();

        //将现在的request 变成 change对象
        return serverWebExchange.mutate().request(request).build();
    }







    /**
     * 方法实现说明:不需要授权的路径
     */
    private boolean shouldSkip(String currentUrl) {
        //路径匹配器(简介SpringMvc拦截器的匹配器)
        //比如/oauth/** 可以匹配/oauth/token    /oauth/check_token等
        PathMatcher pathMatcher = new AntPathMatcher();
        for(String skipPath:notAuthUrlProperties.getShouldSkipUrls()) {
            if(pathMatcher.match(skipPath,currentUrl)) {
                return true;
            }
        }
        return false;
    }



    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 方法实现说明:网关服务启动 生成公钥
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //初始化公钥
        this.publicKey = JwtUtils.genPulicKey(restTemplate);
    }

}
