package com.gw.gwmall.ordercurrent.feignapi.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


/**
* @desc: 类的描述:Feign调用添加请求头
*/
@Slf4j
public class HeaderInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes != null){
            HttpServletRequest request = attributes.getRequest();
            log.info("从Request中解析请求头:{}",request.getHeader("memberId"));
            template.header("memberId",request.getHeader("memberId"));
        }
    }
}
