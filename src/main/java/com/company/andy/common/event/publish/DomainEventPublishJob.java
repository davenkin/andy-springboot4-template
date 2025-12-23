package com.company.andy.common.event.publish;

import com.company.andy.common.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.core.LockConfiguration;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofMinutes;
import static java.time.Instant.now;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

// Publishes all staged domain events to the messaging middleware
@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventPublishJob {
    private static final String MIN_START_EVENT_ID = "EVT00000000000000001";
    private static final int MAX_BATCH_SIZE = 500;
    private static final int MAX_FETCH_SIZE = 10000;
    private final LockingTaskExecutor lockingTaskExecutor;
    private final PublishingDomainEventDao publishingDomainEventDao;
    private final DomainEventSender domainEventSender;
    private final TaskExecutor taskExecutor;

    public void publishStagedDomainEvents(int batchSize) {
        if (batchSize > MAX_BATCH_SIZE || batchSize < 1) {
            throw new IllegalArgumentException("batchSize must be greater than or equal to 1 and less than 500.");
        }

        try {
            // Use a distributed lock to ensure only one node runs as a time, otherwise it may result in duplicated events or ordering issue
            var result = lockingTaskExecutor.executeWithLock(() -> doPublishStagedDomainEvents(batchSize),
                    new LockConfiguration(now(), "publish-domain-events", ofMinutes(1), ofMillis(1)));
            List<String> publishedEventIds = result.getResult();
            if (isNotEmpty(publishedEventIds)) {
                log.debug("Published domain events {}.", publishedEventIds);
            }
        } catch (Throwable e) {
            log.error("Error happened while publish domain events.", e);
        }
    }

    private List<String> doPublishStagedDomainEvents(int batchSize) throws ExecutionException, InterruptedException {
        LockAssert.assertLocked();

        int counter = 0;
        String startEventId = MIN_START_EVENT_ID;
        List<CompletableFuture<String>> futures = new ArrayList<>();

        while (true) {
            List<DomainEvent> domainEvents = publishingDomainEventDao.stagedEvents(startEventId, batchSize);
            if (isEmpty(domainEvents)) {
                break;
            }

            for (DomainEvent event : domainEvents) {
                var future = this.domainEventSender.send(event)
                        .whenCompleteAsync((eventId, ex) -> {
                            if (ex == null) {
                                this.publishingDomainEventDao.successPublish(eventId);
                            } else {
                                this.publishingDomainEventDao.failPublish(event.getId());
                                log.error("Error publishing domain event [{}]:", eventId, ex);
                            }
                        }, taskExecutor);
                futures.add(future);
            }

            counter = domainEvents.size() + counter;
            if (counter >= MAX_FETCH_SIZE) {
                break;
            }

            startEventId = domainEvents.get(domainEvents.size() - 1).getId(); // Start event ID for next batch
        }

        CompletableFuture<List<String>> allResults = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
                );
        allResults.join();
        return allResults.get();
    }
}
