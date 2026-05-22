package com.company.andy.common.security;

import com.company.andy.common.model.OrgRole;
import com.company.andy.common.model.actor.ActorSource;
import com.company.andy.common.model.actor.OrgActor;
import com.company.andy.common.tracing.ActorMdcSupport;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.company.andy.common.model.OrgRole.ORG_ADMIN;
import static com.company.andy.common.model.actor.ActorSource.HUMAN_USER;
import static com.company.andy.common.security.SecurityUtils.createActorInitiatorFrom;
import static com.company.andy.common.security.SecurityUtils.getJwtRoles;
import static com.company.andy.common.utils.Constants.*;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

// Convert the default Jwt principal into OrgActor
// Controllers can use "@AuthenticationPrincipal OrgActor actor" to obtain the current actor

@Slf4j
@NullMarked
public class JwtToOrgActorAuthenticationTokenFilter extends OncePerRequestFilter {
    private final static Set<String> ALL_ORG_ROLES = Arrays.stream(OrgRole.values())
            .map(OrgRole::name)
            .collect(toSet());

    private final AuthenticationEntryPoint authenticationEntryPoint;

    public JwtToOrgActorAuthenticationTokenFilter(AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

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
                        ActorAuthenticationToken authenticationToken = createActorAuthenticationToken(jwt, request);
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        ActorMdcSupport.addMdc(authenticationToken.getActor());
                        mdcPopulated = true;
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (AuthenticationException ex) {
            log.error("Authentication failed:", ex);
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, ex);
        } finally {
            if (mdcPopulated) {
                ActorMdcSupport.clearMdc();
            }
        }
    }

    private ActorAuthenticationToken createActorAuthenticationToken(Jwt jwt, HttpServletRequest request) {
        Set<String> jwtRoles = getJwtRoles(jwt).stream().map(String::toUpperCase).collect(toSet());
        boolean isSystemAdmin = jwtRoles.contains(SYSTEM_ADMIN_ROLE);
        String orgId = getOrgId(jwt, isSystemAdmin, request);

        Set<OrgRole> roles = isSystemAdmin ? Set.of(ORG_ADMIN) : jwtRoles.stream()
                .filter(ALL_ORG_ROLES::contains)
                .map(OrgRole::valueOf)
                .collect(toSet());
        List<SimpleGrantedAuthority> authorities = roles.stream().map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role.name())).toList();

        return new ActorAuthenticationToken(
                new OrgActor(
                        jwt.getSubject(),
                        getActorName(jwt),
                        orgId,
                        roles,
                        getActorSource(jwt),
                        createActorInitiatorFrom(request)
                ),
                authorities,
                jwt
        );
    }

    private String getActorName(Jwt jwt) {
        String name = jwt.getClaimAsString(JWT_CLAIM_PREFERRED_USERNAME);
        return isNotBlank(name) ? name : jwt.getSubject();
    }

    private String getOrgId(Jwt jwt, boolean isSystemAdmin, HttpServletRequest request) {
        if (isSystemAdmin) {
            // For system admin, take the orgId from HTTP header first
            String headerOrgId = request.getHeader(SYSTEM_ACTOR_ORG_ID_HEADER);
            if (isNotBlank(headerOrgId)) {
                return headerOrgId;
            }
        }

        String orgId = jwt.getClaimAsString(JWT_CLAIM_ORG_ID);
        if (isBlank(orgId)) {
            if (isSystemAdmin) {
                throw new InvalidBearerTokenException("Cannot obtain an orgId from HTTP header of x-org-id, and Jwt does not contains and orgId claim either.");
            } else {
                throw new InvalidBearerTokenException("Cannot obtain an orgId from Jwt.");
            }
        }
        return orgId;
    }

    private ActorSource getActorSource(Jwt jwt) {
        // advice: decide the actual ActorSource(HUMAN_USER or SERVICE_ACCOUNT) based on Jwt content
        return HUMAN_USER;
    }
}

