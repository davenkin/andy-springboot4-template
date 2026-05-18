package com.company.andy.common.security;

import com.company.andy.common.model.actor.OrgActor;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import static java.util.Objects.requireNonNull;

@Getter
public final class OrgActorAuthenticationToken extends AbstractAuthenticationToken {
    private final OrgActor actor;
    private final Jwt jwt;

    public OrgActorAuthenticationToken(OrgActor actor, Jwt jwt) {
        requireNonNull(actor, "actor must not be null.");
        requireNonNull(jwt, "jwt must not be null.");

        super(actor.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name())).toList());
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
