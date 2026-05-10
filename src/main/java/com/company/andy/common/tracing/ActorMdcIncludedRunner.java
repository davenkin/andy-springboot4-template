package com.company.andy.common.tracing;

import com.company.andy.common.model.operator.Operator;
import org.slf4j.MDC;

import static java.util.Objects.requireNonNull;

public class ActorMdcIncludedRunner {
    private final Operator operator;

    private ActorMdcIncludedRunner(Operator operator) {
        requireNonNull(operator, "operator must not be null");
        this.operator = operator;
    }

    public static ActorMdcIncludedRunner of(Operator operator) {
        return new ActorMdcIncludedRunner(operator);
    }

    public void run(Runnable runnable) {
        MDC.put("actorId", operator.id());
        MDC.put("actorOrgId", operator.orgId());
        MDC.put("actorInitiator", operator.initiator());
        MDC.put("actorSource", operator.source().name());
        MDC.put("actorType", operator.type().name());
        try {
            runnable.run();
        } finally {
            MDC.remove("actorId");
            MDC.remove("actorOrgId");
            MDC.remove("actorInitiator");
            MDC.remove("actorSource");
            MDC.remove("actorType");
        }
    }
}
