package com.company.andy.common.event.publish;

import com.company.andy.common.event.DomainEvent;

import java.util.concurrent.CompletableFuture;

// Send a domain event to the messaging middleware
public interface DomainEventSender {
    CompletableFuture<String> send(DomainEvent domainEvent);
}
