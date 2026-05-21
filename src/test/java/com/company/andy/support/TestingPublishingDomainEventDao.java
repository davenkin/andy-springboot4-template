package com.company.andy.support;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.company.andy.common.configuration.profile.EnableForIT;
import com.company.andy.common.event.DomainEvent;
import com.company.andy.common.event.consume.EventConsumer;
import com.company.andy.common.event.publish.PublishingDomainEventDao;
import com.company.andy.common.tracing.TracingService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@EnableForIT
public class TestingPublishingDomainEventDao extends PublishingDomainEventDao {
  private final static Set<String> serializationTestedDomainEvents = new HashSet<>();
  private final ObjectMapper objectMapper;
  private final EventConsumer eventConsumer;

  public TestingPublishingDomainEventDao(
      MongoTemplate mongoTemplate,
      TracingService tracingService,
      ObjectMapper objectMapper,
      @Lazy EventConsumer eventConsumer) {
    super(mongoTemplate, tracingService);
    this.objectMapper = objectMapper;
    this.eventConsumer = eventConsumer;
  }

  @Override
  public void stage(List<DomainEvent> events) {
    super.stage(events);
    events.forEach(this::testForJsonSerialization);
    events.forEach(eventConsumer::consumeDomainEvent);// bypass Kafka and directly consume the events
  }

  private void testForJsonSerialization(DomainEvent domainEvent) {
    String name = domainEvent.getClass().getName();
    if (!serializationTestedDomainEvents.contains(name)) {
      serializationTestedDomainEvents.add(name);
      String eventJson = objectMapper.writeValueAsString(domainEvent);
      DomainEvent deserialized = objectMapper.readValue(eventJson, DomainEvent.class);
      assertEquals(domainEvent.getClass(), deserialized.getClass());
    }
  }
}
