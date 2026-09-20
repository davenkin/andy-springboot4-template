package com.company.andy.common.security.org;

import com.company.andy.common.configuration.property.CommonProperties;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.security.ActorAuthenticationToken;
import com.company.andy.common.tracing.ActorMdcSupport;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import static com.company.andy.common.model.actor.ActorOrigin.fromOrgApiCall;
import static com.company.andy.common.model.actor.PrincipalType.*;
import static com.company.andy.common.security.SecurityUtils.getJwtPrincipalType;
import static com.company.andy.common.security.SecurityUtils.getJwtUserName;
import static com.company.andy.common.utils.Constants.JWT_CLAIM_ORG_ID;
import static com.company.andy.common.utils.Constants.ORG_ID_HEADER;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

// Convert the default Jwt principal into OrgActor
// Controllers can use "@AuthenticationPrincipal @NotNull OrgActor actor" to obtain the current actor

@Slf4j
public class JwtToOrgActorAuthenticationTokenFilter extends OncePerRequestFilter {
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final CommonProperties commonProperties;

    public JwtToOrgActorAuthenticationTokenFilter(AuthenticationEntryPoint authenticationEntryPoint, CommonProperties commonProperties) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.commonProperties = commonProperties;
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
                    ActorAuthenticationToken authenticationToken = createActorAuthenticationToken(jwt, request);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    ActorMdcSupport.addMdc(authenticationToken.getActor());
                    mdcPopulated = true;
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            log.debug("Authentication failed:", ex);
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, new AuthenticationServiceException("Authentication failed", ex));
        } finally {
            if (mdcPopulated) {
                ActorMdcSupport.clearMdc();
            }
        }
    }

    private ActorAuthenticationToken createActorAuthenticationToken(Jwt jwt, HttpServletRequest request) {
        if (isFromOrgJwtIssuer(jwt)) {
            return createAuthenticationTokenForOrgJwtIssuer(jwt, request);
        }

        if (isFromPlatformJwtIssuer(jwt)) {
            return createAuthenticationTokenForPlatformJwtIssuer(jwt, request);
        }

        throw new InvalidBearerTokenException("Invalid JWT issuer: " + jwt.getIssuer() + ", expected: " + commonProperties.orgJwtIssuer() + " or " + commonProperties.platformJwtIssuer());
    }

    private boolean isFromPlatformJwtIssuer(Jwt jwt) {
        return Objects.equals(requireNonNull(jwt.getIssuer()).toString(), commonProperties.platformJwtIssuer());
    }

    private boolean isFromOrgJwtIssuer(Jwt jwt) {
        return Objects.equals(requireNonNull(jwt.getIssuer()).toString(), commonProperties.orgJwtIssuer());
    }

    private ActorAuthenticationToken createAuthenticationTokenForOrgJwtIssuer(Jwt jwt, HttpServletRequest request) {
        String principalType = getJwtPrincipalType(jwt);

        if (Objects.equals(principalType, MEMBER.name())) {
            return new ActorAuthenticationToken(Actor.createMemberActor(
                    jwt.getSubject(),
                    getJwtUserName(jwt),
                    getOrgIdFromJwt(jwt),
                    Set.of(),
                    fromOrgApiCall(request)
            ), Set.of(), jwt);
        }

        if (Objects.equals(principalType, ORG_SERVICE_CLIENT.name())) {
            return new ActorAuthenticationToken(Actor.createOrgServiceClientActor(
                    jwt.getSubject(),
                    getOrgIdFromJwt(jwt),
                    fromOrgApiCall(request)
            ), Set.of(), jwt);
        }
        throw new InvalidBearerTokenException("Invalid JWT principal type: " + principalType + " for org issuer, expected: " + MEMBER.name() + " or " + ORG_SERVICE_CLIENT.name());
    }

    private ActorAuthenticationToken createAuthenticationTokenForPlatformJwtIssuer(Jwt jwt, HttpServletRequest request) {
        String principalType = getJwtPrincipalType(jwt);

        if (Objects.equals(principalType, SUPERVISOR.name())) {
            return new ActorAuthenticationToken(Actor.createSupervisedOrgActor(
                    jwt.getSubject(),
                    getJwtUserName(jwt),
                    getOrgIdFromHeader(request),
                    fromOrgApiCall(request)
            ), Set.of(), jwt);
        }

        if (Objects.equals(principalType, PLATFORM_SERVICE_CLIENT.name())) {
            return new ActorAuthenticationToken(Actor.createPlatformServiceClientOrgActor(
                    jwt.getSubject(),
                    getOrgIdFromHeader(request),
                    fromOrgApiCall(request)
            ), Set.of(), jwt);
        }
        throw new InvalidBearerTokenException("Invalid JWT principal type: " + principalType + " for platform issuer, expected: " + SUPERVISOR.name() + " or " + PLATFORM_SERVICE_CLIENT.name());
    }

    private String getOrgIdFromJwt(Jwt jwt) {
        String orgId = jwt.getClaimAsString(JWT_CLAIM_ORG_ID);
        if (isBlank(orgId)) {
            throw new InvalidBearerTokenException("Cannot obtain an orgId from JWT.");
        }
        return orgId;
    }

    private String getOrgIdFromHeader(HttpServletRequest request) {
        String headerOrgId = request.getHeader(ORG_ID_HEADER);

        if (isBlank(headerOrgId)) {
            throw new IllegalStateException("Cannot obtain an orgId from header: " + ORG_ID_HEADER);
        }
        return headerOrgId;
    }
}

