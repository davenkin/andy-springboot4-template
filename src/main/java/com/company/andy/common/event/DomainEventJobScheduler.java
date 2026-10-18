package com.company.andy.common.event;

import com.company.andy.common.event.publish.DomainEventPublisher;
import com.company.andy.common.model.actor.Actor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static com.company.andy.common.model.actor.Actor.createScheduledJobActor;
import static com.company.andy.common.security.CurrentActorService.runWithActor;
import static net.javacrumbs.shedlock.core.LockAssert.assertLocked;

@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventJobScheduler {
    private final DomainEventPublisher domainEventPublisher;
    private final DomainEventHouseKeepingScheduledJob domainEventHouseKeepingScheduledJob;

    // Runs every 5 minutes to publish staged DomainEvents in case the real time publishing mechanism fails
    // This job should not use @SchedulerLock as DomainEventPublisher.publishStagedDomainEvents() already uses an internal distributed lock
    @Scheduled(cron = "0 */5 * * * ?")
    public void houseKeepPublishStagedDomainEvents() {
        log.debug("Start house keep publish DomainEvents.");
        assertLocked();
        Actor actor = createScheduledJobActor("houseKeepPublishStagedDomainEvents");
        runWithActor(actor, () -> domainEventPublisher.publishStagedDomainEvents(100));
    }

    // PublishingDomainEvent and ConsumingEvent are temporary and should be removed regularly
    @Scheduled(cron = "0 10 2 1 * ?")
    @SchedulerLock(name = "removeOldDomainEvents", lockAtMostFor = "PT60M", lockAtLeastFor = "PT1M")
    public void removeOldDomainEvents() {
        assertLocked();
        Actor actor = createScheduledJobActor("removeOldDomainEvents");
        runWithActor(actor, this::doRemoveOldDomainEvents);
    }

    private void doRemoveOldDomainEvents() {
        try {
            domainEventHouseKeepingScheduledJob.removeOldPublishingDomainEvents(180);
        } catch (Throwable t) {
            log.error("Failed remove old publishing DomainEvents from mongo.", t);
        }

        try {
            domainEventHouseKeepingScheduledJob.removeOldConsumingDomainEvents(180);
        } catch (Throwable t) {
            log.error("Failed remove old consuming DomainEvents from mongo.", t);
        }
    }
}
