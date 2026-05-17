package com.company.andy.common.security;

import com.company.andy.common.model.Role;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.model.actor.ActorType;
import com.company.andy.common.tracing.ActorMdcSupport;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.company.andy.common.model.actor.Actor.PLATFORM_ORG_ID;
import static com.company.andy.common.model.actor.ActorType.HUMAN_USER;
import static com.company.andy.common.util.Constants.*;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

// Convert the default Jwt principal into Actor
// Controllers can use "@AuthenticationPrincipal Actor actor" to obtain the current actor

@Slf4j
public class ConvertJwtToActorAuthenticationTokenFilter extends OncePerRequestFilter {
    private final static Set<String> ALL_ROLES = Arrays.stream(Role.values())
            .map(Role::name)
            .collect(toSet());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        boolean mdcPopulated = false;
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
                    Jwt jwt = jwtAuthenticationToken.getToken();
                    if (jwt != null) {
                        Actor actor = Actor.createOrgActor(
                                jwt.getSubject(),
                                getActorName(jwt),
                                getRoles(jwt),
                                getOrgId(jwt),
                                getActorType(jwt),
                                "%s[%s]".formatted(request.getMethod(), request.getRequestURI())
                        );

                        SecurityContextHolder.getContext().setAuthentication(new ActorAuthenticationToken(actor, jwt));
                        ActorMdcSupport.addMdc(actor);
                        mdcPopulated = true;
                    }
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            if (mdcPopulated) {
                ActorMdcSupport.clearMdc();
            }
        }
    }

    private String getActorName(Jwt jwt) {
        String name = jwt.getClaimAsString(JWT_PREFERRED_USERNAME);
        return isNotBlank(name) ? name : jwt.getSubject();
    }

    private Set<Role> getRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap(JWT_REALM_ACCESS);
        if (realmAccess == null) {
            return Set.of();
        }

        Object roles = realmAccess.get(JWT_REALM_ACCESS_ROLES);

        if (roles == null) {
            return Set.of();
        }

        return ((List<String>) roles).stream()
                .filter(ALL_ROLES::contains)
                .map(Role::valueOf)
                .collect(toSet());
    }

    private String getOrgId(Jwt jwt) {
        String orgId = jwt.getClaimAsString(JWT_ORG_ID);
        // todo: deal with empty orgId, should not return PLATFORM_ORG_ID
        return isNotBlank(orgId) ? orgId : PLATFORM_ORG_ID;
    }

    private ActorType getActorType(Jwt jwt) {
        // todo: decide the actual ActorType(HUMAN_USER or SERVICE_ACCOUNT or WEBHOOK_CALLER) based on Jwt content or request URL
        return HUMAN_USER;
    }
}
