package com.company.andy.common.exception;

import com.company.andy.common.tracing.TracingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

import static com.company.andy.common.exception.ServiceException.*;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpStatus.valueOf;


@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final TracingService tracingService;

    @ResponseBody
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<QErrorResponse> handleServiceException(ServiceException ex, HttpServletRequest request) {
        log.error("Error happened while access[{}]: {}", request.getRequestURI(), ex.getMessage(), ex);
        return createErrorResponse(ex, request.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<QErrorResponse> handleAccessDinedException(HttpServletRequest request) {
        return createErrorResponse(accessDeniedException(), request.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<QErrorResponse> handleAuthenticationFailedException(HttpServletRequest request) {
        return createErrorResponse(authenticationException(), request.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<QErrorResponse> handleInvalidRequest(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, Object> error = ex.getBindingResult().getFieldErrors().stream()
                .collect(toImmutableMap(FieldError::getField, fieldError -> {
                    String message = fieldError.getDefaultMessage();
                    return isBlank(message) ? "No message available." : message;
                }, (field1, field2) -> field1 + "|" + field2));

        log.error("Method argument validation error[{}]: {}", ex.getParameter().getParameterType().getName(), error);
        ServiceException exception = requestValidationException(error);
        return createErrorResponse(exception, request.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler({ServletRequestBindingException.class, HttpMessageNotReadableException.class, ConstraintViolationException.class})
    public ResponseEntity<QErrorResponse> handleServletRequestBindingException(Exception ex, HttpServletRequest request) {
        log.error("Request processing error while access[{}]: {}", request.getRequestURI(), ex.getMessage());
        return createErrorResponse(requestValidationException(), request.getRequestURI());
    }

    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> handleGeneralException(Throwable ex, HttpServletRequest request) {
        log.error("Error access[{}]:", request.getRequestURI(), ex);
        return createErrorResponse(systemException(), request.getRequestURI());
    }

    private ResponseEntity<QErrorResponse> createErrorResponse(ServiceException exception, String path) {
        String traceId = tracingService.currentTraceId();
        Error error = new Error(exception, path, traceId);
        QErrorResponse representation = error.toErrorResponse();
        return new ResponseEntity<>(representation, new HttpHeaders(), valueOf(representation.getError().getStatus()));
    }

}
