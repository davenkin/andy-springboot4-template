package com.company.andy.common.tracing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TracingService {

    //get the current trace id
    public String currentTraceId() {
        // todo: impl
        return null;
    }

}
