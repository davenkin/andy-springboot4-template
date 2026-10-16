package com.company.andy.common.security;

import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.model.actor.ActorOriginChannel;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import java.util.UUID;

import static com.company.andy.common.model.actor.Actor.createAnonymousActor;
import static com.company.andy.common.model.actor.ActorOrigin.fromApiCall;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

@NullMarked
public class AnonymousActorAuthenticationTokenFilter extends AnonymousAuthenticationFilter {
    private final String theKey;
    private final ActorOriginChannel channel;

    public AnonymousActorAuthenticationTokenFilter(ActorOriginChannel channel) {
        String key = UUID.randomUUID().toString();
        super(key);
        this.theKey = key;
        this.channel = channel;
    }

    @Override
    protected Authentication createAuthentication(HttpServletRequest request) {
        Actor anonymousActor = createAnonymousActor(fromApiCall(request, this.channel));
        return new AnonymousAuthenticationToken(this.theKey, anonymousActor, createAuthorityList("ROLE_ANONYMOUS"));
    }
}
