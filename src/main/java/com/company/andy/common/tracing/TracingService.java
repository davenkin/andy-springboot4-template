package com.company.andy.common.tracing;

import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TracingService {
    private final Tracer tracer;

    public String currentTraceId() {
        TraceContext context = this.tracer.currentTraceContext().context();
        return context != null ? context.traceId() : null;
    }

}
