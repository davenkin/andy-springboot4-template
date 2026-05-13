package com.company.andy.common.security;

import com.company.andy.common.model.actor.Actor;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static java.util.Objects.requireNonNull;

@Getter
public final class ActorAuthenticationToken extends AbstractAuthenticationToken {
    private final Actor actor;

    public ActorAuthenticationToken(Actor actor) {
        super(actor.roles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList());
        requireNonNull(actor, "User must not be null.");

        this.actor = actor;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
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
