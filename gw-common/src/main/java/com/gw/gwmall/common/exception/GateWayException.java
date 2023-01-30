package com.gw.gwmall.common.exception;


import com.gw.gwmall.common.api.IErrorCode;
import lombok.Data;

/**
*/
@Data
public class GateWayException extends RuntimeException{

    private long code;

    private String message;

    public GateWayException(IErrorCode iErrorCode) {
        this.code = iErrorCode.getCode();
        this.message = iErrorCode.getMessage();
    }
}
