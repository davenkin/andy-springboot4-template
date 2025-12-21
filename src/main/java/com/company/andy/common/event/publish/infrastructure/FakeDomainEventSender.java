package com.company.andy.common.event.publish.infrastructure;

import com.company.andy.common.configuration.profile.EnableForIT;
import com.company.andy.common.event.DomainEvent;
import com.company.andy.common.event.publish.DomainEventSender;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
@Component
@EnableForIT
@RequiredArgsConstructor
public class FakeDomainEventSender implements DomainEventSender {
    private final Map<String, DomainEvent> events = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<String> send(DomainEvent event) {
        this.events.put(event.getId(), event);
        return CompletableFuture.completedFuture(event.getId());
    }

}
