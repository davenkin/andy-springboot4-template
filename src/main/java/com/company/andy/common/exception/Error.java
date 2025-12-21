package com.company.andy.common.exception;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

import static java.time.Instant.now;
import static lombok.AccessLevel.PRIVATE;

@Value
@AllArgsConstructor(access = PRIVATE)
public class Error {
    private final ErrorCode code;
    private final String message;
    private final String userMessage;
    private final int status;
    private final String path;
    private final Instant timestamp;
    private final String traceId;
    private final Map<String, Object> data;

    public Error(ServiceException ex, String path, String traceId) {
        ErrorCode errorCode = ex.getCode();
        this.code = errorCode;
        this.message = ex.getMessage();
        this.userMessage = ex.getUserMessage();
        this.status = errorCode.getStatus();
        this.path = path;
        this.timestamp = now();
        this.traceId = traceId;
        this.data = ex.getData();
    }

    public Error(ErrorCode code, int status, String message, String path, String traceId, Map<String, Object> data) {
        this.code = code;
        this.message = message;
        this.userMessage = message;
        this.status = status;
        this.path = path;
        this.timestamp = now();
        this.traceId = traceId;
        this.data = data;
    }

    public QErrorResponse toErrorResponse() {
        return QErrorResponse.builder().error(this).build();
    }

}
