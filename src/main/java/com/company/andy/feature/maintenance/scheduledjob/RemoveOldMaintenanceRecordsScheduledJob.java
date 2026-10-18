package com.company.andy.feature.maintenance.scheduledjob;

import com.company.andy.feature.maintenance.domain.task.RemoveOldMaintenanceRecordsTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// ScheduledJob is run by Scheduler.

@Slf4j
@Component
@RequiredArgsConstructor
public class RemoveOldMaintenanceRecordsScheduledJob {
    private static final int KEEP_DAYS = 180;
    private final RemoveOldMaintenanceRecordsTask removeOldMaintenanceRecordsTask;

    public void run() {
        this.removeOldMaintenanceRecordsTask.run(KEEP_DAYS);
    }
}
