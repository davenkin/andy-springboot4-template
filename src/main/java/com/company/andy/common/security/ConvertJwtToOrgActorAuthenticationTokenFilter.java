package com.company.andy.common.security;

import com.company.andy.common.model.Role;
import com.company.andy.common.model.actor.ActorSource;
import com.company.andy.common.model.actor.OrgActor;
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

import static com.company.andy.common.model.actor.ActorSource.HUMAN_USER;
import static com.company.andy.common.util.Constants.*;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

// Convert the default Jwt principal into Actor
// Controllers can use "@AuthenticationPrincipal Actor actor" to obtain the current actor

// todo: use authentication entry point for failure

@Slf4j
public class ConvertJwtToOrgActorAuthenticationTokenFilter extends OncePerRequestFilter {
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
                        OrgActor actor = new OrgActor(
                                jwt.getSubject(),
                                getActorName(jwt),
                                getOrgId(jwt),
                                getRoles(jwt),
                                getActorSource(jwt),
                                "%s[%s]".formatted(request.getMethod(), request.getRequestURI())
                        );

                        SecurityContextHolder.getContext().setAuthentication(new OrgActorAuthenticationToken(actor, jwt));
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
        return jwt.getClaimAsString(JWT_ORG_ID); // todo: deal with null
    }

    private ActorSource getActorSource(Jwt jwt) {
        // todo: decide the actual ActorSource(HUMAN_USER or SERVICE_ACCOUNT or WEBHOOK_CALLER) based on Jwt content or request URL
        return HUMAN_USER;
    }
}
