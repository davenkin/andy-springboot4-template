package com.company.andy.common.security;

import static java.util.Objects.requireNonNull;

import java.util.Set;

import com.company.andy.common.model.actor.SystemActor;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

@Getter
public final class SystemActorAuthenticationToken extends AbstractAuthenticationToken {
  private final SystemActor actor;
  private final Jwt jwt;

  public SystemActorAuthenticationToken(SystemActor actor, Jwt jwt) {
    requireNonNull(actor, "actor must not be null.");
    requireNonNull(jwt, "jwt must not be null.");

    super(Set.of());
    this.actor = actor;
    this.jwt = jwt;
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return jwt;
  }

  @Override
  public Object getPrincipal() {
    return actor;
  }

  @Override
  public void eraseCredentials() {
    super.eraseCredentials();
  }
}
