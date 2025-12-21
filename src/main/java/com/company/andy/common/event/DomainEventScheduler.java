package com.company.andy.common.event;

import com.company.andy.common.configuration.profile.DisableForIT;
import com.company.andy.common.event.publish.DomainEventPublishJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@DisableForIT
@RequiredArgsConstructor
public class DomainEventScheduler {
    private final DomainEventPublishJob domainEventPublishJob;
    private final DomainEventHouseKeepingJob domainEventHouseKeepingJob;

    // Runs every 5 minutes to publish staged domain events in case the real time publishing mechanism fails
    // This job should not use @SchedulerLock as DomainEventPublisher.publishStagedDomainEvents() already uses an internal distributed lock
    @Scheduled(cron = "0 */5 * * * ?")
    public void houseKeepPublishStagedDomainEvents() {
        log.debug("Start house keep publish domain events.");
        domainEventPublishJob.publishStagedDomainEvents(100);
    }

    // PublishingDomainEvent and ConsumingEvent are temporary and should be removed regularly
    @Scheduled(cron = "0 10 2 1 * ?")
    @SchedulerLock(name = "removeOldDomainEvents", lockAtMostFor = "PT60M", lockAtLeastFor = "PT1M")
    public void removeOldDomainEvents() {
        LockAssert.assertLocked();
        try {
            domainEventHouseKeepingJob.removeOldPublishingDomainEventsFromMongo(100);
        } catch (Throwable t) {
            log.error("Failed remove old publishing domain events from mongo.", t);
        }

        try {
            domainEventHouseKeepingJob.removeOldConsumingDomainEventsFromMongo(100);
        } catch (Throwable t) {
            log.error("Failed remove old consuming domain events from mongo.", t);
        }
    }
}
