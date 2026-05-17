package com.company.andy.common.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NullMarked
@RequiredArgsConstructor
public class StartupInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final CacheClearingInitializer cacheClearingInitializer;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        cacheClearingInitializer.clearCaches();
        log.info("System initialized after startup.");
    }

}
