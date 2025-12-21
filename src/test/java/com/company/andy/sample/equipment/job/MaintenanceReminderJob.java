package com.company.andy.sample.equipment.job;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class MaintenanceReminderJob {

    public void run() {
        log.info("MaintenanceReminderJob started.");

        //do something

        log.info("MaintenanceReminderJob ended.");
    }
}
