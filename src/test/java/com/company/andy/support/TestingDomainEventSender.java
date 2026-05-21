package com.company.andy.support;

import com.company.andy.common.configuration.profile.EnableForIT;
import com.company.andy.common.event.publish.DomainEventSender;
import com.company.andy.common.event.publish.PublishingDomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

// This bean replaces SpringKafkaDomainEventSender for integration testing
// The event will not be sent to Kafka
// Actually in integration tests the application never talks to Kafka, either publishing or consuming

@Slf4j
@Getter
@Component
@EnableForIT
@RequiredArgsConstructor
public class TestingDomainEventSender implements DomainEventSender {
    private final Map<String, PublishingDomainEvent> events = new ConcurrentHashMap<>();
    private final Set<String> errorEventIds = new HashSet<>();

    @Override
    public CompletableFuture<String> send(PublishingDomainEvent publishingDomainEvent) {
        if (this.errorEventIds.contains(publishingDomainEvent.getId())) {
            return CompletableFuture.failedFuture(new RuntimeException("Simulated error in event handler for sending event:  " + publishingDomainEvent.getId()));
        }

        this.events.put(publishingDomainEvent.getId(), publishingDomainEvent);
        return CompletableFuture.completedFuture(publishingDomainEvent.getId());
    }

    public void throwExceptionFor(String eventId) {
        this.errorEventIds.add(eventId);
    }

    public void removeExceptionFor(String eventId) {
        this.errorEventIds.remove(eventId);
    }
}
