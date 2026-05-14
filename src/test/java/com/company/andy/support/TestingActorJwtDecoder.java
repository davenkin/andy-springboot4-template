package com.company.andy.support;

import static java.util.Base64.getDecoder;

import static com.company.andy.common.util.Constants.JWT_ORG_ID;
import static com.company.andy.common.util.Constants.JWT_PREFERRED_USERNAME;
import static com.company.andy.common.util.Constants.JWT_REALM_ACCESS;
import static com.company.andy.common.util.Constants.JWT_REALM_ACCESS_ROLES;

import java.time.Instant;
import java.util.Map;

import com.company.andy.common.configuration.profile.EnableForIT;
import com.company.andy.common.model.actor.Actor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

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
    return Jwt.withTokenValue(actorToken)
        .headers(h -> h.put("alg", "none"))
        .subject(actor.id())
        .claim(JWT_ORG_ID, actor.orgId())
        .claim(JWT_PREFERRED_USERNAME, actor.name())
        .claim(JWT_REALM_ACCESS, Map.of(JWT_REALM_ACCESS_ROLES, actor.roles().stream().map(Enum::name).toList()))
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(600))
        .build();
  }
}
