package com.company.andy.common.tracing;

import org.jspecify.annotations.NullMarked;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;
import java.util.stream.Collectors;

import static com.company.andy.common.tracing.ActorMdcSupport.ACTOR_MDC_KEYS;

@NullMarked
public class ActorMdcPropagatingTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, String> mdcValues = ACTOR_MDC_KEYS.stream()
                .filter(key -> MDC.get(key) != null)
                .collect(Collectors.toMap(key -> key, MDC::get));

        return () -> {
            mdcValues.forEach(MDC::put);
            try {
                runnable.run();
            } finally {
                ACTOR_MDC_KEYS.forEach(MDC::remove);
            }
        };
    }
}
