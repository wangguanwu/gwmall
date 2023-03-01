package com.gw.gwmall.portal.feignapi.ums;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.model.UmsMember;
import com.gw.gwmall.model.UmsMemberReceiveAddress;
import com.gw.gwmall.portal.domain.PortalMemberInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @desc: 远程调用 会员中心获取具体收货地址
*/
@FeignClient(name = "gw-member",path = "/member")
public interface UmsMemberFeignApi {

    @RequestMapping(value = "/address/{id}", method = RequestMethod.GET)
    @ResponseBody
    CommonResult<UmsMemberReceiveAddress> getItem(@PathVariable(value = "id") Long id);

    @RequestMapping(value = "/center/updateUmsMember",method = RequestMethod.POST)
    CommonResult<String> updateUmsMember(@RequestBody UmsMember umsMember);


    @RequestMapping(value = "/center/getMemberInfo", method = RequestMethod.GET)
    @ResponseBody
    CommonResult<PortalMemberInfo> getMemberById();

    @RequestMapping(value = "/address/list", method = RequestMethod.GET)
    @ResponseBody
    CommonResult<List<UmsMemberReceiveAddress>> list();
}
