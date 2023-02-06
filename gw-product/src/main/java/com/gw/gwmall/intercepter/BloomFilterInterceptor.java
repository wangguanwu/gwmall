package com.gw.gwmall.intercepter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.common.constant.RedisKeyPrefixConst;
import com.gw.gwmall.component.BloomRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @description: 拦截器，所有需要查看商品详情的请求必须先过布隆过滤器
 **/
@Slf4j
public class BloomFilterInterceptor implements HandlerInterceptor {

    @Autowired
    private BloomRedisService bloomRedisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String currentUrl = request.getRequestURI();
        PathMatcher matcher = new AntPathMatcher();
        //解析出pathvariable
        Map<String, String> pathVariable = matcher.extractUriTemplateVariables("/pms/productInfo/{id}", currentUrl);
        //布隆过滤器存储在redis中
        if(bloomRedisService.includeByBloomFilter(RedisKeyPrefixConst.PRODUCT_REDIS_BLOOM_FILTER,pathVariable.get("id"))){
            return true;
        }

        /**
         * 存储在本地jvm布隆过滤器中
         */
        /*if(LocalBloomFilter.match(pathVariable.get("id"))){
            return true;
        }*/

        /*
         * 不在本地布隆过滤器当中，直接返回验证失败
         * 设置响应头
         */
        response.setHeader("Content-Type","application/json");
        response.setCharacterEncoding("UTF-8");
        String result = new ObjectMapper().writeValueAsString(CommonResult.validateFailed("产品不存在!"));
        response.getWriter().print(result);
        return false;

    }

}
