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

  public static void runWithMdc(Operator operator,Runnable runnable) {
    addMdc(operator);
    try {
      runnable.run();
    } finally {
      clearMdc();
    }
  }
}
