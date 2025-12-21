package com.company.andy.sample.equipment.job;

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
public class EquipmentScheduler {
    private final MaintenanceReminderJob maintenanceReminderJob;

    @Scheduled(cron = "0 10 2 1 * ?")
    @SchedulerLock(name = "maintenanceReminderJob")
    public void maintenanceReminderJob() {
        LockAssert.assertLocked();
        this.maintenanceReminderJob.run();
    }
}
