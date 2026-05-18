package com.company.andy.support;

import com.company.andy.common.configuration.profile.EnableForIT;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.model.actor.OrgActor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Map;

import static com.company.andy.common.util.Constants.*;
import static java.util.Base64.getDecoder;

// This bean automatically replaces the default JwtDecoder for integration tests,
// enabling integration tests to pass an actor json string as the Authorization header,
// which eliminates the need to retrieve the issuer's public key(security.oauth2.resourceserver.jwt.issuer-uri) for authentication

@Slf4j
@Component
@EnableForIT
@RequiredArgsConstructor
public class TestingActorJwtDecoder implements JwtDecoder {
    private final ObjectMapper objectMapper;

    @Override
    public Jwt decode(String actorToken) throws JwtException {
        Actor actor = this.objectMapper.readValue(getDecoder().decode(actorToken), Actor.class);

        Jwt.Builder builder = Jwt.withTokenValue(actorToken)
                .headers(h -> h.put("alg", "none"))
                .subject(actor.getId())
                .claim(JWT_PREFERRED_USERNAME, actor.getName())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(600));
        if (actor instanceof OrgActor orgActor) {
            builder.claim(JWT_ORG_ID, orgActor.getOrgId())
                    .claim(JWT_REALM_ACCESS, Map.of(JWT_REALM_ACCESS_ROLES, orgActor.getRoles().stream().map(Enum::name).toList()));
        }
        return builder.build();
    }
}
