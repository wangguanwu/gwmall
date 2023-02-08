package com.gw.gwmall.ordercurrent.feignapi.ums;//package com.tuling.tulingmall.feignapi.ums;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.ordercurrent.model.UmsMemberReceiveAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
* @desc: 类的描述:远程调用 会员中心获取具体收获地址
*/
@FeignClient(name = "gw-member",path = "/member")
public interface UmsMemberFeignApi {

    @RequestMapping(value = "/address/{id}", method = RequestMethod.GET)
    @ResponseBody
    CommonResult<UmsMemberReceiveAddress> getItem(@PathVariable(value = "id") Long id);

//    @RequestMapping(value = "/center/updateUmsMember",method = RequestMethod.POST)
//    CommonResult<String> updateUmsMember(@RequestBody UmsMember umsMember);
//
//
//    @RequestMapping(value = "/center/getMemberInfo", method = RequestMethod.GET)
//    @ResponseBody
//    CommonResult<PortalMemberInfo> getMemberById();
//
//    @RequestMapping(value = "/address/list", method = RequestMethod.GET)
//    @ResponseBody
//    CommonResult<List<UmsMemberReceiveAddress>> list();
}
