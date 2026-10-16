package com.company.andy.support;

import com.company.andy.common.configuration.profile.EnableOnlyForIT;
import com.company.andy.common.configuration.property.CommonProperties;
import com.company.andy.common.model.actor.Actor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

import static com.company.andy.common.model.actor.ActorType.*;
import static com.company.andy.common.utils.Constants.*;
import static java.util.Base64.getDecoder;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

// This bean automatically replaces the default JwtDecoder for integration tests,
// enabling integration tests to pass an actor json string as the Authorization header,
// which eliminates the need to retrieve the issuer's actual public key for authentication

@Slf4j
@Component
@NullMarked
@EnableOnlyForIT
@RequiredArgsConstructor
public class TestingActorJwtDecoder implements JwtDecoder {
    private final ObjectMapper objectMapper;
    private final CommonProperties commonProperties;

    @Override
    public Jwt decode(String actorToken) throws JwtException {
        Actor actor = this.objectMapper.readValue(getDecoder().decode(actorToken), Actor.class);

        Jwt.Builder builder = Jwt.withTokenValue(actorToken)
                .headers(h -> h.put("alg", "none"))
                .subject(actor.getId())
                .claim(JWT_CLAIM_ACTOR_TYPE, actor.getType().name())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(600));

        if (isNotBlank(actor.getName())) {
            builder.claim(JWT_CLAIM_PREFERRED_USERNAME, actor.getName());
        }

        if (actor.getType() == ORG_SERVICE_CLIENT || actor.getType() == PLATFORM_SERVICE_CLIENT) {
            builder.claim(JWT_CLAIM_AZP, actor.getId());
        }

        actor.ifMember(memberId -> builder.claim(JWT_CLAIM_MEMBER_ID, memberId));
        actor.ifSupervisor(supervisorId -> builder.claim(JWT_CLAIM_SUPERVISOR_ID, supervisorId));
        actor.ifDevice(deviceId -> builder.claim(JWT_CLAIM_DEVICE_ID, deviceId));
        actor.ifOrgActor(orgId -> builder.claim(JWT_CLAIM_ORG_ID, orgId));

        if (actor.getType() == MEMBER || actor.getType() == ORG_SERVICE_CLIENT) {
            builder.issuer(commonProperties.orgJwtIssuer());
        }

        if (actor.getType() == SUPERVISOR || actor.getType() == PLATFORM_SERVICE_CLIENT) {
            builder.issuer(commonProperties.platformJwtIssuer());
        }

        if (actor.getType() == DEVICE) {
            builder.issuer(commonProperties.deviceJwtIssuer());
        }

        return builder.build();
    }
}
