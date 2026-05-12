package com.company.andy.feature.maintenance.job;

import com.company.andy.common.configuration.profile.DisableForIT;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.tracing.ActorMdcSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import static com.company.andy.common.model.actor.Actor.createJobActor;
import static net.javacrumbs.shedlock.core.LockAssert.assertLocked;

@Slf4j
@DisableForIT
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class MaintenanceRecordJobScheduler {
    private final RemoveOldMaintenanceRecordsJob removeOldMaintenanceRecordsJob;

    @Scheduled(cron = "0 0 2 1 * ?")
    @SchedulerLock(name = "removeOldMaintenanceRecords")
    public void removeOldMaintenanceRecords() {
        assertLocked();

        Actor actor = createJobActor("removeOldMaintenanceRecords");
        ActorMdcSupport.runWithMdc(actor, this.removeOldMaintenanceRecordsJob::run);
    }
}
