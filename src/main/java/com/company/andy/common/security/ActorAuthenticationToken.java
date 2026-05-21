package com.company.andy.common.security;

import static java.util.Objects.requireNonNull;

import java.util.Collection;

import com.company.andy.common.model.actor.Actor;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

@Getter
public final class ActorAuthenticationToken extends AbstractAuthenticationToken {
  private final Actor actor;
  private final Jwt jwt;

  public ActorAuthenticationToken(Actor actor, Collection<? extends GrantedAuthority> authorities, Jwt jwt) {
    requireNonNull(actor, "actor must not be null.");

    super(authorities);
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
