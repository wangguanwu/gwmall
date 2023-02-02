package com.gw.gwmall.feign;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.model.SmsCouponHistory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 */
@FeignClient(value = "gw-promotion",path = "/coupon")
public interface CouponsFeignService {

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    CommonResult<List<SmsCouponHistory>> list(
            @RequestParam(value = "useStatus", required = false) Integer useStatus
            , @RequestHeader("memberId") Long memberId);


    //灰度测试
    @RequestMapping(value = "/gray/{value}", method = RequestMethod.GET)
    @ResponseBody
    String gray(@PathVariable(value = "value") String value);

}
