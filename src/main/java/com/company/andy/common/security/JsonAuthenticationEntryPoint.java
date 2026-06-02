package com.company.andy.common.security;

import com.company.andy.common.exception.ApiError;
import com.company.andy.common.tracing.TracingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;

import static com.company.andy.common.exception.ErrorCode.AUTHENTICATION_FAILED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@NullMarked
@Component
@RequiredArgsConstructor
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;
    private final TracingService tracingService;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(401);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8);
        ApiError error = new ApiError(AUTHENTICATION_FAILED, "Authentication failed.", request.getMethod(), request.getRequestURI(), tracingService.currentTraceId(), null);
        PrintWriter writer = response.getWriter();
        writer.print(objectMapper.writeValueAsString(error.toErrorResponse()));
        writer.flush();
    }
}
