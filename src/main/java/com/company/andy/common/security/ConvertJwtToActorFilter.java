package com.company.andy.common.security;

import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.tracing.ActorMdcSupport;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

import static com.company.andy.common.model.Role.ORG_ADMIN;
import static com.company.andy.common.model.actor.ActorType.HUMAN_USER;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Slf4j
public class ConvertJwtToActorFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // todo: get jwt and convert it into actor
        Actor actor = Actor.createOrgActor(
                "sampleUserId",
                "sampleUserName",
                Set.of(ORG_ADMIN),
                "sampleOrgId",
                HUMAN_USER,
                "%s[%s]".formatted(request.getMethod(), request.getRequestURI())
        );

        try {
            SecurityContextHolder.getContext().setAuthentication(new ActorAuthenticationToken(actor));
        } catch (Throwable ex) {
            SecurityContextHolder.clearContext();
            response.sendError(SC_UNAUTHORIZED);
            return;
        }

        ActorMdcSupport.addMdc(actor);
        try {
            filterChain.doFilter(request, response);
        } finally {
            ActorMdcSupport.clearMdc();
        }
    }
}
