package com.company.andy.common.tracing;

import org.jspecify.annotations.NonNull;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextPropagatingTaskDecorator implements TaskDecorator {
    @Override
    public @NonNull Runnable decorate(@NonNull Runnable runnable) {
        SecurityContext context = SecurityContextHolder.getContext();
        return () -> {
            try {
                SecurityContextHolder.setContext(context);
                runnable.run();
            } finally {
                SecurityContextHolder.clearContext();
            }
        };
    }
}
