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

import static com.company.andy.common.model.actor.ActorOrigin.fromPlatformApiCall;
import static com.company.andy.common.model.actor.ActorType.PLATFORM_SERVICE_CLIENT;
import static com.company.andy.common.model.actor.ActorType.SUPERVISOR;
import static com.company.andy.common.security.SecurityUtils.*;
import static java.util.Objects.requireNonNull;

// Convert JWT into Platform actor
// Controllers can use "@AuthenticationPrincipal @NotNull Actor actor" to obtain the current actor

@Slf4j
@NullMarked
public class JwtToPlatformActorAuthenticationTokenFilter extends AbstractJwtToActorAuthenticationTokenFilter {
    private final CommonProperties commonProperties;

    public JwtToPlatformActorAuthenticationTokenFilter(AuthenticationEntryPoint authenticationEntryPoint, CommonProperties commonProperties) {
        super(authenticationEntryPoint);
        this.commonProperties = commonProperties;
    }

    @Override
    public ActorAuthenticationToken createActorAuthenticationToken(Jwt jwt, HttpServletRequest request) {
        if (!Objects.equals(requireNonNull(jwt.getIssuer()).toString(), commonProperties.platformJwtIssuer())) {
            throw new InvalidBearerTokenException(
                    "Invalid JWT issuer: " + jwt.getIssuer() + ", platform api expects issuer: " + commonProperties.platformJwtIssuer());
        }

        String actorType = getJwtActorType(jwt);
        if (Objects.equals(actorType, SUPERVISOR.name())) {
            return new ActorAuthenticationToken(Actor.createSupervisorActor(
                    getJwtSupervisorId(jwt),
                    getJwtUserName(jwt),
                    fromPlatformApiCall(request)
            ), Set.of(), jwt);
        }

        if (Objects.equals(actorType, PLATFORM_SERVICE_CLIENT.name())) {
            return new ActorAuthenticationToken(Actor.createPlatformServiceClientActor(
                    getJwtClient(jwt),
                    fromPlatformApiCall(request)
            ), Set.of(), jwt);
        }

        throw new InvalidBearerTokenException(
                "Invalid JWT actory type: " + actorType + ", platform api expects actor type: " + SUPERVISOR.name() + " or " +
                PLATFORM_SERVICE_CLIENT.name());
    }
}
