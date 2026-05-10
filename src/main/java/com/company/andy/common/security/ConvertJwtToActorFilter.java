package com.company.andy.common.security;

import com.company.andy.common.model.operator.Operator;
import com.company.andy.common.tracing.ActorMdcSupport;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

import static com.company.andy.common.model.Role.ORG_ADMIN;
import static com.company.andy.common.model.operator.OperatorSource.HUMAN_USER;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

public class ConvertJwtToActorFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // todo: get jwt and convert it into operator
        Operator operator = Operator.createOrgOperator("sampleUserId", "sampleUserName", Set.of(ORG_ADMIN), "sampleOrgId", HUMAN_USER, request.getRequestURI());

        try {
            SecurityContextHolder.getContext().setAuthentication(new ActorAuthenticationToken(operator));
        } catch (Throwable ex) {
            SecurityContextHolder.clearContext();
            response.sendError(SC_UNAUTHORIZED, ex.getMessage());
            return;
        }

        ActorMdcSupport.addMdc(operator);
        try {
            filterChain.doFilter(request, response);
        } finally {
            ActorMdcSupport.clearMdc();
        }
    }
}
