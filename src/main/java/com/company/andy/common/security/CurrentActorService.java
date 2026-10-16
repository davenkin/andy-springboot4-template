package com.company.andy.common.security;

import com.company.andy.common.model.actor.Actor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;

import static com.company.andy.common.tracing.ActorMdcSupport.runWithMdc;
import static java.util.Objects.requireNonNull;

public class CurrentActorService {

    public static Optional<Actor> currentActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof ActorAuthenticationToken actorAuthenticationToken) {
            return Optional.of(actorAuthenticationToken.getActor());
        }
        return Optional.empty();
    }

    public static void setActor(Actor actor) {
        requireNonNull(actor, "actor must not be null");
        SecurityContextHolder.getContext().setAuthentication(new ActorAuthenticationToken(actor, Set.of(), null));
    }

    public static void clearActor() {
        SecurityContextHolder.clearContext();
    }

    public static void runWithActor(Actor actor, Runnable runnable) {
        setActor(actor);
        try {
            runWithMdc(actor, runnable);
        } finally {
            clearActor();
        }
    }

}
