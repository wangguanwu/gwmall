package com.gw.gwmall.common.utils;

import com.gw.gwmall.common.util.JacksonUtils;
import com.gw.gwmall.model.PmsProduct;

/**
 * @author guanwu
 * @created on 2023-02-16 11:18:12
 **/
public class JacksonUtilsTest {

    public static void main(String[] args) {
        PmsProduct product = new PmsProduct();
        product.setBrandId(1L);
        product.setName("小麻花");
        System.out.println(JacksonUtils.toJson(product));
    }
}
