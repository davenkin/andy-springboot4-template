package com.company.andy.common.security;

import com.company.andy.common.model.operator.Operator;
import com.company.andy.common.tracing.ActorMdcIncludedRunner;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

import static com.company.andy.common.model.Role.ORG_ADMIN;
import static com.company.andy.common.model.operator.OperatorSource.HUMAN_USER;


public class ConvertJwtToActorFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // todo: get jwt and convert it into operator
        Operator operator = Operator.createOrgOperator("sampleUserId", "sampleUserName", Set.of(ORG_ADMIN), "sampleOrgId", HUMAN_USER, request.getRequestURI());

        IOException[] ioExceptions = new IOException[1];
        ServletException[] servletExceptions = new ServletException[1];
        ActorMdcIncludedRunner.of(operator).run(() -> {
            try {
                filterChain.doFilter(request, response);
            } catch (IOException e) {
                ioExceptions[0] = e;
            } catch (ServletException e) {
                servletExceptions[0] = e;
            }
        });

        if (ioExceptions[0] != null) throw ioExceptions[0];
        if (servletExceptions[0] != null) throw servletExceptions[0];
    }
}
