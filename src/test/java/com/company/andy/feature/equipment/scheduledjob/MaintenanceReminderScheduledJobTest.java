package com.company.andy.feature.equipment.scheduledjob;

import com.company.andy.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MaintenanceReminderScheduledJobTest extends IntegrationTest {
    @Autowired
    private MaintenanceReminderScheduledJob maintenanceReminderScheduledJob;

    @Test
    void should_run_maintenance_reminder_job() {
        maintenanceReminderScheduledJob.run();
    }
}
