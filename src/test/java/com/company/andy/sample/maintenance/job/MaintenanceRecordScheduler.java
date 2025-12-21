package com.company.andy.sample.maintenance.job;

import com.company.andy.common.configuration.profile.DisableForIT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@DisableForIT
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class MaintenanceRecordScheduler {
    private final RemoveOldMaintenanceRecordsJob removeOldMaintenanceRecordsJob;

    @Scheduled(cron = "0 0 2 1 * ?")
    @SchedulerLock(name = "removeOldMaintenanceRecordsJob")
    public void removeOldMaintenanceRecordsJob() {
        LockAssert.assertLocked();
        this.removeOldMaintenanceRecordsJob.run();
    }
}
