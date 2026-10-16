package com.company.andy.common.security;

import com.company.andy.common.tracing.ActorMdcSupport;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j
@NullMarked
public abstract class AbstractJwtToActorAuthenticationTokenFilter extends OncePerRequestFilter {
    private final AuthenticationEntryPoint authenticationEntryPoint;

    protected AbstractJwtToActorAuthenticationTokenFilter(AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        boolean mdcPopulated = false;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
                Jwt jwt = jwtAuthenticationToken.getToken();
                ActorAuthenticationToken authenticationToken = createActorAuthenticationToken(jwt, request);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                ActorMdcSupport.addMdc(authenticationToken.getActor());
                mdcPopulated = true;
            }
            filterChain.doFilter(request, response);
        } catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, ex);
        } catch (Exception ex) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, new AuthenticationServiceException(ex.getMessage(), ex));
        } finally {
            if (mdcPopulated) {
                ActorMdcSupport.clearMdc();
            }
        }
    }

    protected abstract ActorAuthenticationToken createActorAuthenticationToken(Jwt jwt, HttpServletRequest request);
}

