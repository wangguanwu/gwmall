package com.gw.gwmall.exception;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.DefaultBlockRequestHandler;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.fastjson.JSON;
import com.gw.gwmall.common.api.CommonResult;
import com.gw.gwmall.common.api.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

/**
 */
@Component
public class GatewayBlockExceptionHandler extends DefaultBlockRequestHandler {

    private static final String DEFAULT_BLOCK_MSG_PREFIX = "Blocked by Sentinel: ";

    @Override
    public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable ex) {
        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(buildErrorResult(ex)));
    }

    private CommonResult buildErrorResult(Throwable ex) {
        if (ex instanceof ParamFlowException) {
            return  CommonResult.failed(ResultCode.TOMANY_REQUEST_ERROR);
        } else if (ex instanceof DegradeException) {
            return CommonResult.failed(ResultCode.BACKGROUD_DEGRADE_ERROR);
        } else {
            return CommonResult.failed(ResultCode.BAD_GATEWAY);
        }
    }
}
