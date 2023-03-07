package com.gw.gwmall.search.service;

import com.gw.gwmall.search.vo.ESRequestParam;
import com.gw.gwmall.search.vo.ESResponseResult;


/**
 * @author gw
 */
public interface EsSearchService {


    /**
     * @param param 检索的所有参数
     * @return  返回检索的结果，里面包含页面需要的所有信息
     */
    ESResponseResult search(ESRequestParam param);


}


