package com.gw.gwmall.search.component.rocketmq;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gw.gwmall.common.util.JacksonUtils;
import com.gw.gwmall.common.vo.ESProductUpdateParam;
import com.gw.gwmall.search.domain.EsProduct;
import com.gw.gwmall.search.service.EsProductDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author guanwu
 * @created on 2023-03-13 16:42:36
 * 监听商品消息
 **/

@Component
@RocketMQMessageListener(topic = "${rocketmq.gwmall.product-change-topic}",
        consumerGroup = "${rocketmq.gwmall.product-change-consumer-group}")
@Slf4j
public class RocketMqProductUpdateMessageListener implements RocketMQListener<String> {

    @Resource
    private EsProductDataService esProductDataService;

    @Override
    public void onMessage(String message) {
        List<ESProductUpdateParam> updateList =
                JacksonUtils.toObj(message, new TypeReference<List<ESProductUpdateParam>>() {});

        log.info("监听到MQ消息:[{}]", updateList);
        List<EsProduct> esProducts = esProductDataService.batchChangeEsProductListInfo(updateList);
        log.info("处理结果: [{}]", esProducts);
    }
}
