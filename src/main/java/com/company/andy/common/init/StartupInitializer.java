package com.company.andy.common.init;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

import static com.company.andy.common.util.Constants.CHINA_TIME_ZONE;
import static java.util.TimeZone.getTimeZone;
import static java.util.TimeZone.setDefault;

@Slf4j
@Component
@NullMarked
@RequiredArgsConstructor
public class StartupInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final CacheClearingInitializer cacheClearingInitializer;

    @PostConstruct
    void init() {
        setDefault(getTimeZone(ZoneId.of(CHINA_TIME_ZONE)));
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        cacheClearingInitializer.clearCaches();
        log.info("System initialized after startup.");
    }

}
