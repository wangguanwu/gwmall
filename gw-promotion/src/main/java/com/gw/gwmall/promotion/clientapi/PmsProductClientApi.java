package com.gw.gwmall.promotion.clientapi;

import com.gw.gwmall.model.PmsBrand;
import com.gw.gwmall.model.PmsProduct;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @desc: 类的描述:Feign远程调用商品服务接口
 */
@FeignClient(name = "gw-product",path = "pms"/*, configuration = FeignConfig.class*/)
public interface PmsProductClientApi {

    @RequestMapping(value = "/getRecommandBrandList", method = RequestMethod.POST)
    @ResponseBody
    List<PmsBrand> getRecommandBrandList(@RequestParam(value="brandIdList") List<Long> brandIdList);

    @RequestMapping(value = "/getProductBatch", method = RequestMethod.POST)
    @ResponseBody
    List<PmsProduct> getProductBatch(@RequestParam(value="productIdList") List<Long> productIdList);
}
