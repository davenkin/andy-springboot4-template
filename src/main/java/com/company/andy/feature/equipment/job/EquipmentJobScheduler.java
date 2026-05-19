package com.company.andy.feature.equipment.job;

import static com.company.andy.common.model.actor.SystemActor.createJobSystemActor;
import static net.javacrumbs.shedlock.core.LockAssert.assertLocked;

import com.company.andy.common.model.actor.SystemActor;
import com.company.andy.common.tracing.ActorMdcSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class EquipmentJobScheduler {
  private final MaintenanceReminderJob maintenanceReminderJob;

  @Scheduled(cron = "0 10 2 1 * ?")
  @SchedulerLock(name = "remindForEquipmentMaintenance")
  public void remindForEquipmentMaintenance() {
    assertLocked();

    SystemActor actor = createJobSystemActor("remindForEquipmentMaintenance");
    ActorMdcSupport.runWithMdc(actor, this.maintenanceReminderJob::run);
  }
}
