package com.company.andy.common.tracing;

import com.company.andy.common.model.operator.Operator;
import org.slf4j.MDC;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class ActorMdcSupport {
    public static final List<String> ACTOR_MDC_KEYS = List.of(
            "actorId",
            "actorOrgId",
            "actorInitiator",
            "actorSource"
    );

    private final Operator operator;

    private ActorMdcSupport(Operator operator) {
        requireNonNull(operator, "operator must not be null");
        this.operator = operator;
    }

    public static ActorMdcSupport of(Operator operator) {
        return new ActorMdcSupport(operator);
    }

    public static void addMdc(Operator operator) {
        MDC.put("actorInitiator", operator.initiator());
        MDC.put("actorSource", operator.source().name());
        if (operator.isOrgOperator()) {
            MDC.put("actorId", operator.id());
            MDC.put("actorOrgId", operator.orgId());
        }
    }

    public static void clearMdc() {
        ACTOR_MDC_KEYS.forEach(MDC::remove);
    }

    public void run(Runnable runnable) {
        addMdc(this.operator);
        try {
            runnable.run();
        } finally {
            clearMdc();
        }
    }
}
