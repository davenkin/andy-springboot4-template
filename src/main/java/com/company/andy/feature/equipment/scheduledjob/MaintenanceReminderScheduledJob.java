package com.company.andy.feature.equipment.scheduledjob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// ScheduledJob is run by Scheduler

@Slf4j
@Component
@RequiredArgsConstructor
public class MaintenanceReminderScheduledJob {

    public void run() {
        log.info("MaintenanceReminderJob started.");

        //do something

        log.info("MaintenanceReminderJob ended.");
    }
}
