# Common coding practices

- Do not rely on database to generate IDs, instead, generate IDs within the code
  using [SnowflakeIdGenerator.newSnowflakeId()](../src/main/java/com/company/andy/common/utils/SnowflakeIdGenerator.java).
  This
  means when the object is created,
  its ID should already been generated in the constructor. Reason: This decouples the code from database implementations
  and also makes
  testing much easier.

```java
public Equipment(String name, Actor actor) {
  super(newEquipmentId(), actor);
  this.name = name;
  raiseEvent(new EquipmentCreatedEvent(this, actor));
}

public static String newEquipmentId() {
  return "EQP" + newSnowflakeId(); // Generate ID in the code
}
```

- Prefer using Java Record over Lombok for value objects. Reason: Records are Java's built-in support, they are more
  concise
  and embodies common best practices like immutability.

```java
public record QDetailedEquipment(
        String id,
        String orgId,
        String name,
        EquipmentStatus status,
        Instant createdAt,
        String createdBy) {}
```

- Never use Lombok's `@Setter` and `@Data`(which implicitly creates setters). Reason: Setters are bad as they break the
  principles of cohesion and information hiding. Also, objects with setters are just data containers like C's struct,
  they do not convey any business intent, making the code hard to understand.
- Always use [ServiceException](../src/main/java/com/company/andy/common/exception/ServiceException.java) for raising
  exceptions, don't create your own exception classes. Reason: The
  `ServiceException` is a flat exception model that makes exception modeling much easier than hierarchical exceptions.

```java
someMethod() {
  throw new ServiceException(EQUIPMENT_NAME_ALREADY_EXISTS,
      "Equipment Name Already Exists.",
      NullableMapUtils.mapOf(AggregateRoot.Fields.id, equipment.getId(), Equipment.Fields.name, newName));
}
```

- All pagination request use HTTP POST method. The query class should
  extend [PageQuery](../src/main/java/com/company/andy/common/utils/PageQuery.java) which has the following
  pagination fields:
    - `pageNumber`: the zero-based page index
    - `pageSize`: the page size
    - `sortField`: the field name to be sorted
    - `sortOrder`: the sorting order
    - The following annotations are required on all pagination requests:
        - `@Getter`: for getter field values
        - `@SuperBuilder`: for builder
        - `@EqualsAndHashCode(callSuper = true)`: for equals() and hashcode()
        - `@NoArgsConstructor(access = PRIVATE)`: for Json deserialization

Example query
class [PageEquipmentsQuery](../src/main/java/com/company/andy/feature/equipment/query/PageEquipmentsQuery.java):

```java
@Getter
@SuperBuilder
@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator)
public class PageEquipmentsQuery extends PageQuery {
  @Schema(description = "Search text")
  @Max(50)
  private String search;

  @Schema(description = "Equipment status to query")
  private EquipmentStatus status;
}
```

The controller receives a query object using POST method:

```java
    @Operation(summary = "Query equipments")
    @PostMapping("/paged")
    public PagedResponse<QPagedEquipment> pageEquipments(@RequestBody @Valid PageEquipmentsQuery query, @AuthenticationPrincipal Actor actor) {
        return this.equipmentQueryService.pageEquipments(query, actor);
    }
```

All pagination response should return [PagedResponse](../src/main/java/com/company/andy/common/utils/PagedResponse.java).

- Use Java 8's `Instant` to represent timestamp, don't use `OffsetDateTime` or `ZonedDateTime`. Reason: `Instant` is
  designed for such purpose, there is no point in storing timezone information inside a timestamp.
- Set application default timezone to 'UTC' explicitly by `TimeZone.setDefault(TimeZone.getTimeZone("UTC"))`. Reason: A
  unified default timezone makes time handling much easier.

```java
@SpringBootApplication
public class SpringBootWebApplication {
  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC")); // Set default timezone to 'UTC'
    SpringApplication.run(SpringBootWebApplication.class, args);
  }
}
```

- Use Lombok's `@FieldNameConstants` to access objects' field names. For example, when accessing MongoDB, filed names
  are usually needed.

```java
@FieldNameConstants
public class Equipment extends AggregateRoot {
  private EquipmentStatus status;
}
```

```java
private someMethod() {
  // Use "Equipment.Fields.status" to access Equipment's "status" field
  criteria.and(Equipment.Fields.status).is(query.status());
}
```

- Do not
  use [Spring Data Repository](https://docs.spring.io/spring-data/commons/reference/repositories/query-methods-details.html).
  Reason: Spring Data's auto generated repository query method names can be very long and hard to read, also it cannot
  survive code refactoring. Instead, implement your own repository classes which
  extends [AbstractMongoRepository](../src/main/java/com/company/andy/common/infrastructure/AbstractMongoRepository.java),
  this
  gives you more freedom.

```java
@Repository
@RequiredArgsConstructor
public class EquipmentRepository extends AbstractMongoRepository<Equipment> {}
```

- Use a single instance of `ObjectMapper` across the whole application as much as possible. Reason: A single
  `ObjectMapper` behaves
  the same for all scenarios.
  In [CommonConfiguration](../src/main/java/com/company/andy/common/configuration/CommonConfiguration.java) a
  `JsonMapperBuilderCustomizer` is created for building an `ObjectMapper`:

```java
    @Bean
    public JsonMapperBuilderCustomizer jsonMapperBuilderCustomizer() {
        return builder -> builder
                .changeDefaultVisibility(it -> it.withVisibility(ALL, ANY))
                .changeDefaultPropertyInclusion(it -> it.withValueInclusion(ALWAYS))
                .enable(REQUIRE_SETTERS_FOR_GETTERS)
                .disable(FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(WRITE_DATES_AS_TIMESTAMPS)
                .disable(WRITE_DURATIONS_AS_TIMESTAMPS);
    }
```

Here, `changeDefaultVisibility()` is used to enable direct field access(
`checker.withVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)`), which means there is no
need to
expose getters/setters.

- Always enable transaction in CommandServices by using `@Transactional`.

```java
@Transactional
public String createEquipment(CreateEquipmentCommand command, Actor actor) {
  Equipment equipment = equipmentFactory.create(command.name(), actor);
  equipmentRepository.save(equipment);
  log.info("Created Equipment[{}].", equipment.getId());
  return equipment.getId();
}
```

- If distributed lock is required, used
  Shedlock's [LockingTaskExecutor](../src/main/java/com/company/andy/common/configuration/DistributedLockConfiguration.java).
- Make configuration files, e.g. `application.yaml` as simple as possible, prefer using constants in the code for configuration items that seldom change.
- Do not create interface classes for services until really needed. Reason: the public methods on service classes
  already serve
  as interfaces.
- Use [Actor](../src/main/java/com/company/andy/common/model/actor/Actor.java) to pass current user context
  around, do
  not use Spring Security's `SecurityContextHolder` for retrieving user information. Reason:
  `SecurityContextHolder`s are essentially thread scoped global variables, it makes the code implicit and also makes
  testing harder. 
- Use [RestClient](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-restclient) for
  calling remote APIs. Do not use Webclient as it's from the Webflux ecosystem. Do not use RestTemplate as is already
  marked as deprecated. Do not use HTTP Service Clients (`@HttpExchange` etc.) as it requires extra configuration.
- There are two [TaskExecutors](../src/main/java/com/company/andy/common/configuration/TaskExecutionConfiguration.java) in the application, choose them wisely:
  - `applicationTaskExecutor`: this is the primary one, it uses virtual threads, generally you should use this one, especially for I/O intensive tasks.
  - `threadPoolTaskExecutor`: this is a classic thread pool based executor, it should only be used for CPU intensive tasks.
- Single item configuration properties should be put under `common` section in `applciation.yaml` and [CommonProperties].(../src/main/java/com/company/andy/common/configuration/property/CommonProperties.java)
- All caches should be configured in [CacheConfiguration](../src/main/java/com/company/andy/common/configuration/CacheConfiguration.java) before use, otherwise the jackson serialization may not work properly.
- [SpringKafkaEventListener](../src/main/java/com/company/andy/common/event/consume/infrastructure/SpringKafkaEventListener.java) and [SpringKafkaDomainEventSender](../src/main/java/com/company/andy/common/event/publish/infrastructure/SpringKafkaDomainEventSender.java) are the only two places where Kafka is touched, stick to this as this minimizes the coupling with Kafka and also makes it easier to switch to other messaging systems in the future if needed.
- When implementing your own event handler, just extend [AbstractEventHandler](../src/main/java/com/company/andy/common/event/consume/AbstractEventHandler.java), but you should carefully think about whether to override the following methods:
```java
    public boolean isIdempotent() {
        // By default, all handlers are assumed to be not idempotent by themselves
        return false;
    }

    public boolean isTransactional() {
        // By default, all handlers are assumed to be transactional, we should make handlers to be transactional as much as possible
        return true;
    }

    public int priority() {
        // Smaller value means higher priority and will be handled first
        return 0;
    }
```
- For event consuming, [EventConsumer](../src/main/java/com/company/andy/common/event/consume/EventConsumer.java) is the central place where all kinds of events (internal domain events, external events etc.) are consumed.
- For domain event publishing, events are first staged(saved) in the database within the same transaction of business processing, and then published asynchronously triggered by MongoDB's [Change Stream](https://www.mongodb.com/docs/manual/changestreams/). The Change Stream configuration can be found in [EventConfiguration](../src/main/java/com/company/andy/common/event/EventConfiguration.java).
- In integration tests, Kafka is not enabled, so we need to manually trigger event consuming by calling `EventConsumer.consumeDomainEvent()` for consuming domain events and other methods for other types of events.
- [Mongock](https://mongock.io/) is used for database migration, please refer to [Migration001_Sample](../src/main/java/com/company/andy/common/migration/Migration001_Sample.java) as a template when creating your own migrations.
- Database indexes should be managed by Mongock migrations, take [Migration002_BaseSetup](../src/main/java/com/company/andy/common/migration/Migration002_BaseSetup.java) as an example. Also, make sure that every index has a specified name for better index maintenability.
- [Actor](../src/main/java/com/company/andy/common/model/actor/Actor.java) is used to represent anyone/anything that interacts with the systems, it can be a human user, a background job, or event handler. For security, actors are categorized into three types:
  - `OrgActor`: represents an organization actor, it always carries an `orgId`;
  - `SystemActor`: represents the system itself, and also can impersonate an `OrgActor`;
  - `AnonymousActor`: represents an anonymous actor.
- [RateLimiter](../src/main/java/com/company/andy/common/ratelimiter/RateLimiter.java) can be used for rate limiting. Use it inside CommandService and QueryService.
- When adding new MongoDB collections, make sure pre-create the collection in [ApplicationInitializer.ensureMongoCollectionsExists()](../src/main/java/com/company/andy/common/init/ApplicationInitializer.java).