package com.gw.gwmall.cart.feignapi.pms;

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.cart.domain.CartProduct;
import com.gw.gwmall.cart.domain.CartPromotionItem;
import com.gw.gwmall.cart.domain.PmsProductParam;
import com.gw.gwmall.cart.domain.PromotionProduct;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @desc: 类的描述:调用商品服务接口

*/
@FeignClient(name = "gw-product")
public interface PmsProductFeignApi {

    @RequestMapping(value = "/pms/cartProduct/{productId}", method = RequestMethod.GET)
    @ResponseBody
    CommonResult<CartProduct> getCartProduct(@PathVariable("productId") Long productId);

    @RequestMapping(value = "/pms/getPromotionProductList", method = RequestMethod.GET)
    CommonResult<List<PromotionProduct>> getPromotionProductList(@RequestParam("productIds") List<Long> ids);

    @RequestMapping("/stock/lockStock")
    CommonResult lockStock(@RequestBody List<CartPromotionItem> cartPromotionItemList);

    @RequestMapping(value = "/pms/productInfo/{id}", method = RequestMethod.GET)
    @ResponseBody
    CommonResult<PmsProductParam> getProductInfo(@PathVariable("id") Long id);

    @RequestMapping(value = "/stock/selectStock", method = RequestMethod.GET)
    @ResponseBody
    CommonResult<Integer> selectStock(@RequestParam("productId") Long productId,
                                             @RequestParam(value = "flashPromotionRelationId") Long flashPromotionRelationId);
}
