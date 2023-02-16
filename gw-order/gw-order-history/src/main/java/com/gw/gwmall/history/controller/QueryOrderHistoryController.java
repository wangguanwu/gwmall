package com.gw.gwmall.history.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 查询历史订单Controller
 */
@Slf4j
@Controller
@Api(tags = "QueryOrderHistoryController",description = "查询历史订单")
@RequestMapping("/order/history")
public class QueryOrderHistoryController {

    // todo 实现对历史订单的查询

}
