package com.company.andy.common.exception;

import java.time.Instant;
import java.util.Map;

import static java.time.Instant.now;

public record Error(
        ErrorCode code,
        String message,
        String userMessage,
        int status,
        String path,
        Instant timestamp,
        String traceId,
        Map<String, Object> data) {

    public Error(ServiceException ex, String path, String traceId) {
        ErrorCode errorCode = ex.getCode();
        this(errorCode, ex.getMessage(), ex.getUserMessage(), errorCode.getStatus(), path, now(), traceId, ex.getData());
    }

    public Error(ErrorCode code, String message, String path, String traceId, Map<String, Object> data) {
        this(code, message, message, code.getStatus(), path, now(), traceId, data);
    }

    public QErrorResponse toErrorResponse() {
        return new QErrorResponse(this);
    }
}
