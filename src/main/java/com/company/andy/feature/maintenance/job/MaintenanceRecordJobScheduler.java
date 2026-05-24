package com.company.andy.feature.maintenance.job;

import com.company.andy.common.model.actor.SystemActor;
import com.company.andy.common.tracing.ActorMdcSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;

import static com.company.andy.common.model.actor.SystemActor.createJobSystemActor;
import static net.javacrumbs.shedlock.core.LockAssert.assertLocked;

@Slf4j
@Profile("local | it | it-local")
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class MaintenanceRecordJobScheduler {
    private final RemoveOldMaintenanceRecordsJob removeOldMaintenanceRecordsJob;

    @Scheduled(cron = "0 0 2 1 * ?")
    @SchedulerLock(name = "removeOldMaintenanceRecords")
    public void removeOldMaintenanceRecords() {
        assertLocked();

        SystemActor actor = createJobSystemActor("removeOldMaintenanceRecords");
        ActorMdcSupport.runWithMdc(actor, this.removeOldMaintenanceRecordsJob::run);
    }
}
