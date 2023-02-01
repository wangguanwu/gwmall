package com.gw.gwmall.mall.feign;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.mall.model.UmsMember;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "gw-member",path="/member/center")
public interface UmsMemberFeignService {
    
    @RequestMapping("/loadUmsMember")
    CommonResult<UmsMember> loadUserByUsername(@RequestParam("username") String username);
}