package com.company.andy.common.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import static com.company.andy.common.exception.ErrorCode.*;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

// This is the only exception that you should throw as we follow a flat exception model
// When throw, pass in an ErrorCode that categorize the error
@Getter
public final class ServiceException extends RuntimeException {
    private final ErrorCode code;
    private final Map<String, Object> data = new HashMap<>();
    private String message;
    private final String userMessage;

    public ServiceException(ErrorCode code, String userMessage) {
        this.code = code;
        this.userMessage = userMessage;
        this.message = formatMessage(userMessage);
    }

    public ServiceException(ErrorCode code, String userMessage, Throwable cause) {
        super(cause);
        this.code = code;
        this.userMessage = userMessage;
        this.message = formatMessage(userMessage);
    }

    public ServiceException(ErrorCode code, String userMessage,
                            String key, Object value) {
        this.code = code;
        addData(key, value);
        this.userMessage = userMessage;
        this.message = formatMessage(userMessage);
    }

    public ServiceException(ErrorCode code, String userMessage,
                            String key, Object value,
                            Throwable cause) {
        super(cause);
        this.code = code;
        addData(key, value);
        this.userMessage = userMessage;
        this.message = formatMessage(userMessage);
    }

    public ServiceException(ErrorCode code, String userMessage,
                            String key1, Object value1,
                            String key2, Object value2) {
        this.code = code;
        addData(key1, value1);
        addData(key2, value2);
        this.userMessage = userMessage;
        this.message = formatMessage(userMessage);
    }

    public ServiceException(ErrorCode code, String userMessage,
                            String key1, Object value1,
                            String key2, Object value2,
                            Throwable cause) {
        super(cause);
        this.code = code;
        addData(key1, value1);
        addData(key2, value2);
        this.userMessage = userMessage;
        this.message = formatMessage(userMessage);
    }

    public ServiceException(ErrorCode code, String userMessage,
                            String key1, Object value1,
                            String key2, Object value2,
                            String key3, Object value3) {
        this.code = code;
        addData(key1, value1);
        addData(key2, value2);
        addData(key3, value3);
        this.userMessage = userMessage;
        this.message = formatMessage(userMessage);
    }


    public ServiceException(ErrorCode code, String userMessage,
                            String key1, Object value1,
                            String key2, Object value2,
                            String key3, Object value3,
                            Throwable cause) {
        super(cause);
        this.code = code;
        addData(key1, value1);
        addData(key2, value2);
        addData(key3, value3);
        this.userMessage = userMessage;
        this.message = formatMessage(userMessage);
    }

    public ServiceException(ErrorCode code, String userMessage,
                            String key1, Object value1,
                            String key2, Object value2,
                            String key3, Object value3,
                            String key4, Object value4) {
        this.code = code;
        addData(key1, value1);
        addData(key2, value2);
        addData(key3, value3);
        addData(key4, value4);
        this.userMessage = userMessage;
        this.message = formatMessage(userMessage);
    }

    public ServiceException(ErrorCode code, String userMessage,
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
        this.userMessage = userMessage;
        this.message = formatMessage(userMessage);
    }

    public ServiceException(ErrorCode code, String userMessage,
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
        this.userMessage = userMessage;
        this.message = formatMessage(userMessage);
    }

    public ServiceException(ErrorCode code, String userMessage,
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
        this.userMessage = userMessage;
        this.message = formatMessage(userMessage);
    }


    public ServiceException(ErrorCode code, String userMessage, Map<String, Object> data) {
        this.code = code;
        this.data.putAll(data);
        this.userMessage = userMessage;
        this.message = formatMessage(userMessage);
    }

    public ServiceException(ErrorCode code, String userMessage, Map<String, Object> data, Throwable cause) {
        super(cause);
        this.code = code;
        this.data.putAll(data);
        this.userMessage = userMessage;
        this.message = formatMessage(userMessage);
    }

    private String formatMessage(String userMessage) {
        StringBuilder stringBuilder = new StringBuilder().append("[").append(this.code.toString()).append("]");

        if (isNotBlank(userMessage)) {
            stringBuilder.append(userMessage);
        }

        if (isNotEmpty(this.data)) {
            stringBuilder.append("Data: ").append(this.data);
        }

        return stringBuilder.toString();
    }

    public static ServiceException accessDeniedException() {
        return new ServiceException(ACCESS_DENIED, "Access Denied.");
    }

    public static ServiceException authenticationException() {
        return new ServiceException(AUTHENTICATION_FAILED, "Authentication failed.");
    }

    public static ServiceException requestValidationException() {
        return new ServiceException(REQUEST_VALIDATION_FAILED, "Request validation failed.");
    }

    public static ServiceException requestValidationException(Map<String, Object> data) {
        return new ServiceException(REQUEST_VALIDATION_FAILED, "Request validation failed.", data);
    }

    public void addData(String key, Object value) {
        this.data.put(key, value);
        this.message = formatMessage(this.userMessage);
    }

}
