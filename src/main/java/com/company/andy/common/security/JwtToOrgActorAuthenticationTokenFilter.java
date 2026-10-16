package com.company.andy.common.security;

import com.company.andy.common.configuration.property.CommonProperties;
import com.company.andy.common.model.actor.Actor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.util.Objects;
import java.util.Set;

import static com.company.andy.common.model.actor.ActorOrigin.fromOrgApiCall;
import static com.company.andy.common.model.actor.ActorType.*;
import static com.company.andy.common.security.SecurityUtils.*;
import static com.company.andy.common.utils.Constants.ORG_ID_HEADER;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

// Convert JWT into Org actor
// Controllers can use "@AuthenticationPrincipal @NotNull Actor actor" to obtain the current actor

@Slf4j
@NullMarked
public class JwtToOrgActorAuthenticationTokenFilter extends AbstractJwtToActorAuthenticationTokenFilter {
    private final CommonProperties commonProperties;

    public JwtToOrgActorAuthenticationTokenFilter(AuthenticationEntryPoint authenticationEntryPoint, CommonProperties commonProperties) {
        super(authenticationEntryPoint);
        this.commonProperties = commonProperties;
    }

    @Override
    protected ActorAuthenticationToken createActorAuthenticationToken(Jwt jwt, HttpServletRequest request) {
        if (isFromOrgJwtIssuer(jwt)) {
            return createAuthenticationTokenForOrgJwtIssuer(jwt, request);
        }

        if (isFromPlatformJwtIssuer(jwt)) {
            return createAuthenticationTokenForPlatformJwtIssuer(jwt, request);
        }

        throw new InvalidBearerTokenException(
                "Invalid JWT issuer: " + jwt.getIssuer() + ", org api expects issuer: " + commonProperties.orgJwtIssuer() + " or " +
                commonProperties.platformJwtIssuer());
    }

    private boolean isFromPlatformJwtIssuer(Jwt jwt) {
        return Objects.equals(requireNonNull(jwt.getIssuer()).toString(), commonProperties.platformJwtIssuer());
    }

    private boolean isFromOrgJwtIssuer(Jwt jwt) {
        return Objects.equals(requireNonNull(jwt.getIssuer()).toString(), commonProperties.orgJwtIssuer());
    }

    private ActorAuthenticationToken createAuthenticationTokenForOrgJwtIssuer(Jwt jwt, HttpServletRequest request) {
        String actorType = getJwtActorType(jwt);

        if (Objects.equals(actorType, MEMBER.name())) {
            return new ActorAuthenticationToken(Actor.createMemberActor(
                    getJwtMemberId(jwt),
                    getJwtUserName(jwt),
                    getJwtOrgId(jwt),
                    fromOrgApiCall(request)
            ), Set.of(), jwt);
        }

        if (Objects.equals(actorType, ORG_SERVICE_CLIENT.name())) {
            return new ActorAuthenticationToken(Actor.createOrgServiceClientActor(
                    getJwtClient(jwt),
                    getJwtOrgId(jwt),
                    fromOrgApiCall(request)
            ), Set.of(), jwt);
        }
        throw new InvalidBearerTokenException(
                "Invalid JWT actor type: " + actorType + " for org issuer, expected: " + MEMBER.name() + " or " +
                ORG_SERVICE_CLIENT.name());
    }

    private ActorAuthenticationToken createAuthenticationTokenForPlatformJwtIssuer(Jwt jwt, HttpServletRequest request) {
        String actorType = getJwtActorType(jwt);

        if (Objects.equals(actorType, SUPERVISOR.name())) {
            return new ActorAuthenticationToken(Actor.createSupervisorActorForOrg(
                    getJwtSupervisorId(jwt),
                    getJwtUserName(jwt),
                    getOrgIdFromHeader(request),
                    fromOrgApiCall(request)
            ), Set.of(), jwt);
        }

        if (Objects.equals(actorType, PLATFORM_SERVICE_CLIENT.name())) {
            return new ActorAuthenticationToken(Actor.createPlatformServiceClientActorForOrg(
                    getJwtClient(jwt),
                    getOrgIdFromHeader(request),
                    fromOrgApiCall(request)
            ), Set.of(), jwt);
        }
        throw new InvalidBearerTokenException(
                "Invalid JWT actor type: " + actorType + " for platform issuer, expected: " + SUPERVISOR.name() + " or " +
                PLATFORM_SERVICE_CLIENT.name());
    }

    private String getOrgIdFromHeader(HttpServletRequest request) {
        String headerOrgId = request.getHeader(ORG_ID_HEADER);

        if (isBlank(headerOrgId)) {
            throw new IllegalStateException("Cannot obtain an orgId from header: " + ORG_ID_HEADER);
        }
        return headerOrgId;
    }
}

