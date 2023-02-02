package com.gw.gwmall.promotion.clientapi;//package com.tuling.tulingmall.promotion.clientapi;
//
//
//import com.tuling.tulingmall.common.api.CommonResult;
//import com.tuling.tulingmall.promotion.model.UmsMember;
//import com.tuling.tulingmall.promotion.clientapi.interceptor.config.FeignConfig;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//
///**
//* @desc: 类的描述:Feign远程调用用户服务接口
//*/
//@FeignClient(name = "tulingmall-member", configuration = FeignConfig.class)
//public interface UmsMemberClientApi {
//
//    @RequestMapping(value = "/getCurrentMember", method = RequestMethod.GET)
//    @ResponseBody
//    CommonResult<UmsMember> getCurrentMember();
//}
