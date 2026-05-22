package com.company.andy.feature.equipment.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MaintenanceReminderJob {

  public void run() {
    log.info("MaintenanceReminderJob started.");

    //do something

    log.info("MaintenanceReminderJob ended.");
  }
}
