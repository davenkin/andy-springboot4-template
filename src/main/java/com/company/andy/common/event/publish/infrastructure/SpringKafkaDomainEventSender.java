package com.company.andy.common.event.publish.infrastructure;

import com.company.andy.common.configuration.profile.DisableForIT;
import com.company.andy.common.event.DomainEvent;
import com.company.andy.common.event.publish.DomainEventSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static com.company.andy.common.util.Constants.KAFKA_DOMAIN_EVENT_TOPIC;

// Send domain events to Kafka
// This is the only place where event publishing touches Kafka, hence the coupling to Kafka is minimised
@Slf4j
@Component
@DisableForIT
@RequiredArgsConstructor
public class SpringKafkaDomainEventSender implements DomainEventSender {
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @Override
    public CompletableFuture<String> send(DomainEvent event) {
        return this.kafkaTemplate.send(KAFKA_DOMAIN_EVENT_TOPIC, event.getArId(), event)
                .thenApply(record -> record.getProducerRecord().value().getId());
    }
}
