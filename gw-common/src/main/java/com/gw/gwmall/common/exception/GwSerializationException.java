package com.gw.gwmall.common.exception;


/**
 * @author guanwu
 * @created on 2023-02-15 19:33:45
 **/
public class GwSerializationException extends GwRuntimeException {
    public static final long errorCode = 100;

    private static final long serialVersionUID = -6860737116199729620L;

    private static final String DEFAULT_MSG = "Gw serialize failed. ";

    private static final String MSG_FOR_SPECIFIED_CLASS = "Gw serialize for class [%s] failed. ";

    private Class<?> serializedClass;

    public GwSerializationException() {
        super(errorCode);
    }

    public GwSerializationException(Class<?> serializedClass) {
        super(errorCode, String.format(MSG_FOR_SPECIFIED_CLASS, serializedClass.getName()));
        this.serializedClass = serializedClass;
    }

    public GwSerializationException(Class<?> serializedClass, Throwable cause) {
        super(errorCode, String.format(MSG_FOR_SPECIFIED_CLASS, serializedClass.getName()), cause);
    }

    public GwSerializationException(Throwable cause) {
        super(errorCode, DEFAULT_MSG, cause);
    }

    public Class<?> getSerializedClass() {
        return serializedClass;
    }
}
