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

import static com.company.andy.common.model.actor.ActorOrigin.fromDeviceApiCall;
import static com.company.andy.common.security.SecurityUtils.getJwtDeviceId;
import static com.company.andy.common.security.SecurityUtils.getJwtOrgId;
import static java.util.Objects.requireNonNull;

// Convert JWT into a Device actor
// Controllers can use "@AuthenticationPrincipal @NotNull Actor actor" to obtain the current actor

@Slf4j
@NullMarked
public class JwtToDeviceActorAuthenticationTokenFilter extends AbstractJwtToActorAuthenticationTokenFilter {
    private final CommonProperties commonProperties;

    public JwtToDeviceActorAuthenticationTokenFilter(AuthenticationEntryPoint authenticationEntryPoint, CommonProperties commonProperties) {
        super(authenticationEntryPoint);
        this.commonProperties = commonProperties;
    }

    @Override
    protected ActorAuthenticationToken createActorAuthenticationToken(Jwt jwt, HttpServletRequest request) {
        if (!Objects.equals(requireNonNull(jwt.getIssuer()).toString(), commonProperties.deviceJwtIssuer())) {
            throw new InvalidBearerTokenException(
                    "Invalid JWT issuer: " + jwt.getIssuer() + ", device api expects issuer: " + commonProperties.deviceJwtIssuer());
        }

        return new ActorAuthenticationToken(Actor.createDeviceActor(getJwtDeviceId(jwt),
                getJwtOrgId(jwt),
                fromDeviceApiCall(request)),
                Set.of(),
                jwt);
    }

}
