package com.gw.gwmall.common.exception;

/**
 * @author guanwu
 * @created on 2023-02-15 19:35:33
 **/
public class GwRuntimeException extends RuntimeException {
    public static final String ERROR_MESSAGE_FORMAT = "errCode: %d, errMsg: %s ";
    private static final long serialVersionUID = -5864472623932075613L;

    private long errCode;

    public GwRuntimeException(long errCode) {
        super();
        this.errCode = errCode;
    }

    public GwRuntimeException(long errCode, String message) {
        super(String.format(ERROR_MESSAGE_FORMAT, errCode, message));
    }

    public GwRuntimeException(long errCode, String message, Throwable cause) {
        super(String.format(ERROR_MESSAGE_FORMAT,errCode, message), cause);
    }

    public GwRuntimeException(long errCode, Throwable cause) {
        super(cause);
    }

    public long getErrCode() {
        return errCode;
    }

    public void setErrCode(long errCode) {
        this.errCode = errCode;
    }
}
