package com.company.andy.common.tracing;

import org.jspecify.annotations.NullMarked;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NullMarked
public class ActorMdcPropagatingTaskDecorator implements TaskDecorator {
    private static final List<String> MDC_KEYS = List.of(
            "actorId",
            "actorOrgId",
            "actorInitiator",
            "actorSource",
            "actorType"
    );

    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, String> mdcValues = MDC_KEYS.stream()
                .filter(key -> MDC.get(key) != null)
                .collect(Collectors.toMap(key -> key, MDC::get));

        return () -> {
            mdcValues.forEach(MDC::put);
            try {
                runnable.run();
            } finally {
                MDC_KEYS.forEach(MDC::remove);
            }
        };
    }
}
