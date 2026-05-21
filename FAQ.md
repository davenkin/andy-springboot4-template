### How Kafka is disabled in integration tests?

- In `application-it.yaml` or `application-it-local.yaml`, the KafkaAutoConfiguration is excluded which disables the consuming side:

```yaml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration # Disable Kafka
```

- Also, `EventConfiguration` is disabled using `@DisableForIT` which disables the publishing side:

```java
@Slf4j
@DisableForIT
@Configuration(proxyBeanMethods = false)
public class EventConfiguration {
    // ...
}
```

### How Redis server is set up in integration tests?

- The `TestingEmbeddedRedisServer` is enabled for `it` profile(not `it-local` profile) and it starts an embedded Redis
  server using
  `@PostConstruct`:

```java
    @PostConstruct
    public synchronized void startRedisServer() {
        // ...
    }
```

### How to make multiple domain events inherit from the same base class (not the DomainEvent base class)?

- Please refer
  to [EquipmentUpdatedEvent](src/main/java/com/company/andy/feature/org/equipment/domain/event/EquipmentUpdatedEvent.java)
  as an example, where:
    - [EquipmentUpdatedEvent](src/main/java/com/company/andy/feature/org/equipment/domain/event/EquipmentUpdatedEvent.java)
      is the base class which itself inherits from [DomainEvent](src/main/java/com/company/andy/common/event/DomainEvent.java)
    - [EquipmentStatusUpdatedEvent](src/main/java/com/company/andy/feature/org/equipment/domain/event/EquipmentStatusUpdatedEvent.java)
      and [EquipmentNameUpdatedEvent](src/main/java/com/company/andy/feature/org/equipment/domain/event/EquipmentNameUpdatedEvent.java)
      are the subclasses
      of [EquipmentUpdatedEvent](src/main/java/com/company/andy/feature/org/equipment/domain/event/EquipmentUpdatedEvent.java)

### How to make multiple event handlers handle the same event independently?

- Just add multiple event handler classes with the same event type, and you are ready to go.
- Example:
  both [EquipmentCreatedEventHandler](src/main/java/com/company/andy/feature/org/equipment/eventhandler/EquipmentCreatedEventHandler.java)
  and [EquipmentCreatedAnotherEventHandler](src/main/java/com/company/andy/feature/org/equipment/eventhandler/EquipmentCreatedAnotherEventHandler.java)
  handle the same
  event [EquipmentCreatedEvent](src/main/java/com/company/andy/feature/org/equipment/domain/event/EquipmentCreatedEvent.java)
  independently.