package com.company.andy.common.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import static com.company.andy.common.exception.ErrorCode.*;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

// This is the only exception that you should throw as we follow a flat exception model
// When throw, pass in an ErrorCode that categorize the error
@Getter
public final class ServiceException extends RuntimeException {
    private final ErrorCode code;
    private Map<String, Object> data;
    private final String message;
    private final String detailMessage;

    public ServiceException(ErrorCode code, String message) {
        this.code = code;
        this.message = message;
        this.detailMessage = toDetailMessage(message);
    }

    public ServiceException(ErrorCode code, String message, Throwable cause) {
        super(cause);
        this.code = code;
        this.message = message;
        this.detailMessage = toDetailMessage(message);
    }

    public ServiceException(ErrorCode code, String message,
                            String key, Object value) {
        this.code = code;
        addData(key, value);
        this.message = message;
        this.detailMessage = toDetailMessage(message);
    }

    public ServiceException(ErrorCode code, String message,
                            String key, Object value,
                            Throwable cause) {
        super(cause);
        this.code = code;
        addData(key, value);
        this.message = message;
        this.detailMessage = toDetailMessage(message);
    }

    public ServiceException(ErrorCode code, String message,
                            String key1, Object value1,
                            String key2, Object value2) {
        this.code = code;
        addData(key1, value1);
        addData(key2, value2);
        this.message = message;
        this.detailMessage = toDetailMessage(message);
    }

    public ServiceException(ErrorCode code, String message,
                            String key1, Object value1,
                            String key2, Object value2,
                            Throwable cause) {
        super(cause);
        this.code = code;
        addData(key1, value1);
        addData(key2, value2);
        this.message = message;
        this.detailMessage = toDetailMessage(message);
    }

    public ServiceException(ErrorCode code, String message,
                            String key1, Object value1,
                            String key2, Object value2,
                            String key3, Object value3) {
        this.code = code;
        addData(key1, value1);
        addData(key2, value2);
        addData(key3, value3);
        this.message = message;
        this.detailMessage = toDetailMessage(message);
    }


    public ServiceException(ErrorCode code, String message,
                            String key1, Object value1,
                            String key2, Object value2,
                            String key3, Object value3,
                            Throwable cause) {
        super(cause);
        this.code = code;
        addData(key1, value1);
        addData(key2, value2);
        addData(key3, value3);
        this.message = message;
        this.detailMessage = toDetailMessage(message);
    }

    public ServiceException(ErrorCode code, String message,
                            String key1, Object value1,
                            String key2, Object value2,
                            String key3, Object value3,
                            String key4, Object value4) {
        this.code = code;
        addData(key1, value1);
        addData(key2, value2);
        addData(key3, value3);
        addData(key4, value4);
        this.message = message;
        this.detailMessage = toDetailMessage(message);
    }

    public ServiceException(ErrorCode code, String message,
                            String key1, Object value1,
                            String key2, Object value2,
                            String key3, Object value3,
                            String key4, Object value4,
                            Throwable cause) {
        super(cause);
        this.code = code;
        addData(key1, value1);
        addData(key2, value2);
        addData(key3, value3);
        addData(key4, value4);
        this.message = message;
        this.detailMessage = toDetailMessage(message);
    }

    public ServiceException(ErrorCode code, String message,
                            String key1, Object value1,
                            String key2, Object value2,
                            String key3, Object value3,
                            String key4, Object value4,
                            String key5, Object value5) {
        this.code = code;
        addData(key1, value1);
        addData(key2, value2);
        addData(key3, value3);
        addData(key4, value4);
        addData(key5, value5);
        this.message = message;
        this.detailMessage = toDetailMessage(message);
    }

    public ServiceException(ErrorCode code, String message,
                            String key1, Object value1,
                            String key2, Object value2,
                            String key3, Object value3,
                            String key4, Object value4,
                            String key5, Object value5,
                            Throwable cause) {
        super(cause);
        this.code = code;
        addData(key1, value1);
        addData(key2, value2);
        addData(key3, value3);
        addData(key4, value4);
        addData(key5, value5);
        this.message = message;
        this.detailMessage = toDetailMessage(message);
    }


    public ServiceException(ErrorCode code, String message, Map<String, Object> data) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.detailMessage = toDetailMessage(message);
    }

    public ServiceException(ErrorCode code, String message, Map<String, Object> data, Throwable cause) {
        super(cause);
        this.code = code;
        this.data = data;
        this.message = message;
        this.detailMessage = toDetailMessage(message);
    }

    public static ServiceException accessDeniedException() {
        return new ServiceException(ACCESS_DENIED, "Access Denied.");
    }

    public static ServiceException authenticationException() {
        return new ServiceException(AUTHENTICATION_FAILED, "Authentication failed.");
    }

    public static ServiceException notFoundException() {
        return new ServiceException(NOT_FOUND, "Not found.");
    }

    public static ServiceException requestValidationException() {
        return new ServiceException(REQUEST_VALIDATION_FAILED, "Request validation failed.");
    }

    public static ServiceException requestValidationException(Map<String, Object> data) {
        return new ServiceException(REQUEST_VALIDATION_FAILED, "Request validation failed.", data);
    }

    public static ServiceException systemException() {
        return new ServiceException(SYSTEM_ERROR, "System error.");
    }

    private String toDetailMessage(String message) {
        StringBuilder stringBuilder = new StringBuilder().append("[").append(this.code.toString()).append("]");

        if (isNotBlank(message)) {
            stringBuilder.append(message);
        }

        if (isNotEmpty(this.data)) {
            stringBuilder.append("|Data: ").append(this.data);
        }

        return stringBuilder.toString();
    }

    public void addData(String key, Object value) {
        requireNonNull(key, "key should not be null");

        if (this.data == null) {
            this.data = new HashMap<>();
        }
        this.data.put(key, value);
    }

}
