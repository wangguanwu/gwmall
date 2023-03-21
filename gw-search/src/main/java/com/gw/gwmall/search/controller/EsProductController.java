package com.gw.gwmall.search.controller;

import com.alibaba.fastjson.JSON;
import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.common.util.JacksonUtils;
import com.gw.gwmall.common.vo.ESProductUpdateParam;
import com.gw.gwmall.search.domain.EsProduct;
import com.gw.gwmall.search.service.EsProductDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 搜索商品管理Controller
 */
@Controller
@Api(tags = "EsProductController", description = "搜索商品管理")
@RequestMapping("/esProduct")
@Slf4j
public class EsProductController {

    @Autowired
    private EsProductDataService esProductDataService;

    @ApiOperation(value = "根据id创建商品")
    @RequestMapping(value = "/create/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String create(@PathVariable Long id) {
        EsProduct esProduct = esProductDataService.create(id);

        return JSON.toJSONString(esProduct);
    }

    @ApiOperation(value = "根据id删除商品")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String delete(@PathVariable Long id) {
        esProductDataService.delete(id);
        return "";
    }

    @ApiOperation(value = "根据id批量删除商品")
    @RequestMapping(value = "/delete/batch", method = RequestMethod.GET)
    @ResponseBody
    public String delete(@RequestParam("ids") List<Long> ids) {
        esProductDataService.delete(ids);
        return "";
    }

    @ApiOperation(value = "简单搜索")
    @RequestMapping(value = "/search/simple", method = RequestMethod.GET)
    @ResponseBody
    public String search(@RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false, defaultValue = "0") Integer pageNum,
                                                      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        Page<EsProduct> esProductPage = esProductDataService.search(keyword, pageNum, pageSize);
        log.info("esProductPage.toString():"+esProductPage.toString()+" esProductPage.getContent():"+esProductPage.getContent().size());
        return esProductPage.getContent().toString();
    }

    @ApiOperation(value = "上传商品信息")
    @RequestMapping(value = "/uploadAllProduct", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<List<EsProduct>> uploadAllProduct() {
        return CommonResult.success(esProductDataService.uploadAllProduct());
    }

    @ApiOperation(value = "批量变更ES商品信息")
    @RequestMapping(value = "/batchChangeProductList", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<List<EsProduct>> batchUploadProductList(@RequestBody List<ESProductUpdateParam> updateParamList) {
        CommonResult<List<EsProduct>> success = CommonResult.success(esProductDataService.batchChangeEsProductListInfo(updateParamList));
        log.info("{}", JacksonUtils.toJson(success));
        return success;
    }
}
