package com.company.andy.common.tracing;

import com.company.andy.common.model.actor.Actor;
import org.slf4j.MDC;

import java.util.List;

public class ActorMdcSupport {
    public static final List<String> ACTOR_MDC_KEYS = List.of(
            "actorId",
            "actorOrgId",
            "actorInitiator",
            "actorType"
    );

    public static void addMdc(Actor actor) {
        if (actor.isOrgActor()) {
            MDC.put("actorId", actor.id());
            MDC.put("actorOrgId", actor.orgId());
        }
        MDC.put("actorInitiator", actor.initiator());
        MDC.put("actorType", actor.source().name());
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
