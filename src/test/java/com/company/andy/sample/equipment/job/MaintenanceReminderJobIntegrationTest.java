package com.company.andy.sample.equipment.job;

import com.company.andy.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MaintenanceReminderJobIntegrationTest extends IntegrationTest {
    @Autowired
    private MaintenanceReminderJob maintenanceReminderJob;

    @Test
    void should_run_maintenance_reminder_job() {
        maintenanceReminderJob.run();
    }
}
