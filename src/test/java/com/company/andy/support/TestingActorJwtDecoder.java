package com.company.andy.support;

import static java.util.Base64.getDecoder;

import static com.company.andy.common.model.actor.PrincipalType.MEMBER;
import static com.company.andy.common.model.actor.PrincipalType.ORG_SERVICE_CLIENT;
import static com.company.andy.common.model.actor.PrincipalType.PLATFORM_SERVICE_CLIENT;
import static com.company.andy.common.model.actor.PrincipalType.SUPERVISOR;
import static com.company.andy.common.utils.Constants.JWT_CLAIM_ORG_ID;
import static com.company.andy.common.utils.Constants.JWT_CLAIM_PREFERRED_USERNAME;
import static com.company.andy.common.utils.Constants.JWT_CLAIM_PRINCIPAL_TYPE;
import static com.company.andy.common.utils.Constants.JWT_CLAIM_REALM_ACCESS;
import static com.company.andy.common.utils.Constants.JWT_CLAIM_REALM_ACCESS_ROLES;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.time.Instant;
import java.util.Map;

import com.company.andy.common.configuration.profile.EnableOnlyForIT;
import com.company.andy.common.configuration.property.CommonProperties;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.model.actor.OrgActor;
import com.company.andy.common.model.actor.PlatformActor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

// This bean automatically replaces the default JwtDecoder for integration tests,
// enabling integration tests to pass an actor json string as the Authorization header,
// which eliminates the need to retrieve the issuer's actual public key for authentication

@Slf4j
@Component
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
        .claim(JWT_CLAIM_PRINCIPAL_TYPE, actor.getPrincipalType().name())
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(600));

    if (isNotBlank(actor.getName())) {
      builder.claim(JWT_CLAIM_PREFERRED_USERNAME, actor.getName());
    }

    if (actor instanceof OrgActor orgActor) {
      builder.claim(JWT_CLAIM_ORG_ID, orgActor.getOrgId());
      if (actor.getPrincipalType() == MEMBER || actor.getPrincipalType() == ORG_SERVICE_CLIENT) {
        builder.claim(JWT_CLAIM_REALM_ACCESS, Map.of(JWT_CLAIM_REALM_ACCESS_ROLES, orgActor.getRoles().stream().map(Enum::name).toList()))
            .issuer(commonProperties.orgJwtIssuer());
      } else {
        builder.issuer(commonProperties.platformJwtIssuer());
      }
    } else if (actor instanceof PlatformActor platformActor &&
               (actor.getPrincipalType() == SUPERVISOR || actor.getPrincipalType() == PLATFORM_SERVICE_CLIENT)) {
      builder.claim(JWT_CLAIM_REALM_ACCESS,
              Map.of(JWT_CLAIM_REALM_ACCESS_ROLES, platformActor.getRoles().stream().map(Enum::name).toList()))
          .issuer(commonProperties.platformJwtIssuer());
    }

    return builder.build();
  }
}
