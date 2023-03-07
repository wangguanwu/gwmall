package com.gw.gwmall.search.controller;

/**
 * @author guanwu
 *
 */

import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.search.service.EsSearchService;
import com.gw.gwmall.search.vo.ESRequestParam;
import com.gw.gwmall.search.vo.ESResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class EsSearchController {

     @Autowired
     private EsSearchService esSearchService;

    /**
     * 自动将页面提交过来的所有请求参数封装成我们指定的对象
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/searchList")
    public CommonResult<ESResponseResult> listPage(ESRequestParam param, HttpServletRequest request) {

        //1、根据传递来的页面的查询参数，去es中检索商品
        ESResponseResult searchResult = esSearchService.search(param);

        return CommonResult.success(searchResult);
    }
}
