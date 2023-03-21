package com.gw.gwmall.promotion;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.common.vo.ESProductUpdateParam;
import com.gw.gwmall.domain.EsProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 搜索模块api
 *
 * @author guanwu
 * @created on 2023-03-10 13:03:06
 **/

@FeignClient(name = "gw-search",path = "/esProduct")
public interface SearchFeignApi {
    /*获得秒杀内容*/
    @RequestMapping(value = "/batchChangeProductList", method = RequestMethod.POST)
    @ResponseBody
    CommonResult<List<EsProductResponse>> batchChangeProductList(
            @RequestBody List<ESProductUpdateParam> productUpdateParams);
}
