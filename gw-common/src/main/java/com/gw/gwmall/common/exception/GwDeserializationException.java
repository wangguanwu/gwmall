package com.gw.gwmall.common.exception;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * @author guanwu
 * @created on 2023-02-15 19:47:33
 **/
public class GwDeserializationException extends GwRuntimeException implements Serializable {
    private static final long serialVersionUID = -8265664447249819109L;

    public static final long ERROR_CODE = 101;

    private static final String DEFAULT_MSG = "Gw deserialize failed. ";

    private static final String MSG_FOR_SPECIFIED_CLASS = "Gw deserialize for class [%s] failed. ";

    private Class<?> targetClass;

    public GwDeserializationException() {
        super(ERROR_CODE);
    }

    public GwDeserializationException(Class<?> targetClass) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetClass.getName()));
        this.targetClass = targetClass;
    }

    public GwDeserializationException(Type targetType) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetType.toString()));
    }

    public GwDeserializationException(Throwable throwable) {
        super(ERROR_CODE, DEFAULT_MSG, throwable);
    }

    public GwDeserializationException(Class<?> targetClass, Throwable throwable) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetClass.getName()), throwable);
        this.targetClass = targetClass;
    }

    public GwDeserializationException(Type targetType, Throwable throwable) {
        super(ERROR_CODE, String.format(MSG_FOR_SPECIFIED_CLASS, targetType.toString()), throwable);
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }
}
