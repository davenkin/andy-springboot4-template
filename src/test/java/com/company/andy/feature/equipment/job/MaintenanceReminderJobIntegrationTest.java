package com.company.andy.feature.equipment.job;

import com.company.andy.IntegrationTest;
import com.company.andy.feature.org.equipment.job.MaintenanceReminderJob;
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
