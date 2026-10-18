package com.company.andy.common.tracing;

import com.company.andy.common.model.actor.Actor;
import org.slf4j.MDC;

import java.util.List;

public class ActorMdcSupport {
    private static final List<String> ACTOR_MDC_KEYS = List.of("actor", "actorOrgId", "actorOrigin");

    public static void addMdc(Actor actor) {
        MDC.put("actor", "%s[%s]".formatted(actor.getType(), actor.getId()));
        actor.ifOrgActor(orgActorId -> MDC.put("actorOrgId", orgActorId));
        MDC.put("actorOrigin", actor.getOrigin().toString());
    }

    public static void clearMdc() {
        ACTOR_MDC_KEYS.forEach(MDC::remove);
    }

    public static void runWithMdc(Actor actor, Runnable runnable) {
        addMdc(actor);
        try {
            runnable.run();
        } finally {
            clearMdc();
        }
    }
}
