# Object implementation patterns

## Context

There are various types of objects in software, such as Controllers, Domain Objects, Factory, Repositories and Services,
etc. Their responsibilities and characteristics differ, but the same type of objects share something in common and it's
important that we keep our coding practices consistent for them.

## Decision

For the same type of objects, we follow the same implementation patterns.

## Implementation

- [AggregateRoot](#aggregateroot)
- [Entity under AggregateRoot](#entity-under-aggregateroot)
- [Value object](#value-object)
- [Repository](#repository)
- [Controller](#controller)
- [CommandService](#commandservice)
- [Command](#command)
- [DomainService](#domainservice)
- [DomainEvent](#domainevent)
- [EventHandler](#eventhandler')
- [Factory](#factory)
- [Task](#task)
- [Job](#job)
- [QueryService](#queryservice)
- [Query](#query)

### AggregateRoot

- Aggregate roots are the most important types of objects in your software, they contain your core domain logic, they
  are the sole reason your software exists
- All aggregate roots should extend [AggregateRoot](../src/main/java/com/company/andy/common/model/AggregateRoot.java)
- All changes to the internal state of aggregate roots should go via the public methods of aggregate roots
- Aggregate Root should use meaningful constructors to create itself
- Builders(such as lombok `@Builder`) should not be used for creating aggregate roots, because they can easily result in
  invalid objects, also builders does not convey any business meaning
- All arguments constructor(such as lombok `@AllArgsConstructor`) should not be used for creating aggregate roots,
  because they can easily result in invalid objects, also they does not convey any business meaning
- For code consistency, always use [Factory](#factory) to create aggregate roots, no matter how simple it is
- Aggregate roots should have a globally unique ID and this ID should be generate by the code but not by database
- Aggregate roots should have meaningful business methods for changing its own state. Every business method should
  ensure the object is always in valid state by applying business rules. Business methods might raise domain events
  after state is changed.
- Aggregate roots have the following class level annotations:
    - `@Slf4j`: for logging
    - `@Getter`: for retrieving data (actually getters are quite bad as it violates information hiding principle, but
      for convenience let's keep them)
    - `@FieldNameConstants`: for access filed names in situations like accessing MongoDB
    - `@TypeAlias(EQUIPMENT_COLLECTION)`: use an explict type alias, otherwise the FQCN will be used by Spring Data
      MongoDB which does not survive refactorings of changing package locations
    - `@Document(EQUIPMENT_COLLECTION)`: for MongoDB collection
    - `@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator)`: for Jackson deserialization as well as
      built from MongoDB, should be
      `PRIVATE` as it's not supposed to be called manually
- Aggregate roots should not be annotated with lombok's `@Setter`, `@Builder`, `@Data`, `@Value` or
  `@AllArgsConstructor`

Example aggregate root [Equipment](../src/main/java/com/company/andy/feature/org/equipment/domain/Equipment.java):

```java

@FieldNameConstants // For access field names
@TypeAlias(EQUIPMENT_COLLECTION) // Use a explict type alias
@Document(EQUIPMENT_COLLECTION) // Configure MongoDB collection name
@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator) // For Jackson and MongoDB deserialization
public class Equipment extends AggregateRoot {
    public final static String EQUIPMENT_COLLECTION = "equipment";
    private String name;
    private EquipmentStatus status;
    private String holder;
    private long maintenanceRecordCount;

    public Equipment(String name, Actor actor) { // Explict business contructors
        super(newEquipmentId(), actor);
        this.name = name;
        this.engine = new EquipmentEngine("DEFAULT_ENGINE_MODEL");
        raiseEvent(new EquipmentCreatedEvent(this, actor)); // Raise Domain Event
    }

    public static String newEquipmentId() {
        return "EQP" + newSnowflakeId(); // Generate ID in the code
    }

    public void updateName(String newName) { // Business method
        if (Objects.equals(newName, this.name)) { // Apply business rules
            return;
        }
        this.name = newName; // Update object state 
        raiseEvent(new EquipmentNameUpdatedEvent(name, this)); // Raise domain event
    }
}
```

### Entity under AggregateRoot

- Sometimes aggregate roots have child entities, these entities are mutable objects and they don't have their own
  repository.
- They are only accessed via their parent aggregate root, and they don't have globally unique ID.
- Entities have the following class level annotations:
    - `@Getter` for retrieving data
    - `@FieldNameConstants`: for access filed names in situations like accessing MongoDB
    - `@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator)`: for Jackson deserialization as well as
      built from MongoDB, should be `PRIVATE` as it's not supposed to be called manually

Example entity [EquipmentEngine](src/main/java/com/company/andy/feature/equipment/domain/EquipmentEngine.java):

```java
@Getter
@FieldNameConstants // For access field names
@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator)
public class EquipmentEngine {
    private String model;
    private int temperature;
    private boolean started;

    public EquipmentEngine(String model) {
        this.model = model;
        this.temperature = 0;
        this.started = false;
    }
    // ...
}
```

### Value object

- Value objects are immutable objects that represents a value, they don't have their own identity, and they are usually
  used as fields in aggregate roots or entities.
- Value objects should mainly be implemented as Java Records, and they can have multiple constructors if needed
- Value objects can optionally use builders(such as lombok `@Builder`) for creation, but this is not recommended as it
  can easily result in invalid objects

Example value objects [Actor](src/main/java/com/company/andy/common/model/actor/Actor.java):

```java
public record Actor(String id,
                    String name,
                    Set<Role> roles,
                    String orgId,
                    ActorType type,
                    String initiator,
                    Instant createdAt) {
}
```

### Repository

- Repository abstracts database interactions for accessing aggregate roots
- Every aggregate root class has its own Repository class
- All repository implementations should
  extend [AbstractMongoRepository](../src/main/java/com/company/andy/common/infrastructure/AbstractMongoRepository.java)

Example repository [EquipmentRepository](../src/main/java/com/company/andy/feature/org/equipment/domain/EquipmentRepository.java):

```java
@Repository
@RequiredArgsConstructor
public class EquipmentRepository extends AbstractMongoRepository<Equipment> {
    private final CacheEvictor cacheEvictor;

    @Override
    public void save(Equipment equipment) {
        super.save(equipment);
        evictCachedEquipmentSummaries(equipment.getOrgId());
    }

    @Override
    public void save(List<Equipment> equipments) {
        super.save(equipments);
        equipments.stream().findFirst().ifPresent(it -> evictCachedEquipmentSummaries(it.getOrgId()));
    }
}
```

### Controller

- Controller should be very thin by offloading the work to CommandService for writing data and QueryService for reading
  data
- Controller classes should be annotated with `@Validated` to enable request validation
- Request objects in method parameters should be annotated with `@Valid` to enable request validation
- Controller ensures an [Actor](../src/main/java/com/company/andy/common/model/actor/Actor.java) is injected using
  `@AuthenticationPrincipal`, this `Actor` is then passed down the whole processing flow from the reqeust and passed to
  CommandService or QueryService
- Controller should follow REST principles on naming URLs and choosing HTTP methods

Example controller [EquipmentController](../src/main/java/com/company/andy/feature/org/equipment/controller/EquipmentController.java):

```java
@Validated // To enable request validation
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/equipments")
public class EquipmentController {
    private final EquipmentCommandService equipmentCommandService;
    private final EquipmentQueryService equipmentQueryService;

    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(summary = "Create an equipment")
    public ResponseId createEquipment(@RequestBody @Valid CreateEquipmentCommand command, @AuthenticationPrincipal Actor actor) {
        return new ResponseId(this.equipmentCommandService.createEquipment(command, actor));
    }
}
```

### CommandService

- Command services serves as the facade for the domain model
- Every public method in command services should represent a use case, and should be annotated with `@Transactional` if it
  writes to database
- Methods in command services usually accept a [Command](#command) object as parameter, as well as an `Actor` object
- Command services should not contain business logic but only orchestrate the work of domain model, repositories and other components, the business logic should be in
  aggregate roots or domain services
- Command ServiceS return the aggregate root's ID for creating objects, and return `void` for updating or deleting
  aggregate roots

Example command service [EquipmentCommandService](../src/main/java/com/company/andy/feature/org/equipment/command/EquipmentCommandService.java):

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class EquipmentCommandService {
    private final EquipmentRepository equipmentRepository;
    private final EquipmentFactory equipmentFactory;
    private final EquipmentDomainService equipmentDomainService;

    @Transactional
    public String createEquipment(CreateEquipmentCommand command, Actor actor) {
        Equipment equipment = equipmentFactory.create(command.name(), actor);
        equipmentRepository.save(equipment);
        log.info("Created Equipment[{}].", equipment.getId());
        return equipment.getId();
    }
}
```

### Command

- Command objects are request objects that instruct the software to change its internal state
- Command objects should be modeled as Java Record
- Command objects can be annotated with `@Builder` for testing purpose
- Command objects object should use JSR-303 annotations  (such as `@NotNull`, `@Max` and `@Pattern`) for data validation

Example command object [CreateMaintenanceRecordCommand](../src/main/java/com/company/andy/feature/org/maintenance/command/CreateMaintenanceRecordCommand.java):

```java
@Builder
public record CreateMaintenanceRecordCommand(
        @NotBlank String equipmentId,
        @NotBlank @Size(max = 1000) String description,
        @NotNull EquipmentStatus status
) {
}
```

### DomainService

- Domain services are totally different from command service(or query services) in that domain services are part of the domain
  model, but command services(or query services) are external to domain model
- Domain services hold domain logic
- Use of domain services should be minimized, as domain logic should best be residing in aggregate roots. Domain services are our
  last resort if the business logic is not suitable to be put inside aggregate roots.

Example domain service [EquipmentDomainService](../src/main/java/com/company/andy/feature/org/equipment/domain/EquipmentDomainService.java):

```java
@Component
@RequiredArgsConstructor
public class EquipmentDomainService {
    private final EquipmentRepository equipmentRepository;

    public void updateEquipmentName(Equipment equipment, String newName, Actor actor) {
        if (!Objects.equals(newName, equipment.getName()) &&
            equipmentRepository.existsByName(newName, equipment.getOrgId())) {
            throw new ServiceException(EQUIPMENT_NAME_ALREADY_EXISTS,
                    "Equipment Name Already Exists.",
                    mapOf(AggregateRoot.Fields.id, equipment.getId(), Equipment.Fields.name, newName));
        }

        equipment.updateName(newName, actor);
    }
}
```

In the above example, the business logic of "checking duplicated equipment name" falls outside the ability of
`Equipment`
itself, hence `EquipmentDomainService` is used instead.

### DomainEvent

- All domain events should extend [DomainEvent](../src/main/java/com/company/andy/common/event/DomainEvent.java)
- Domain events should be immutable as it represent something already happened which cannot not be changed
- Every domain event should register its own type
  inside [DomainEventType](../src/main/java/com/company/andy/common/event/DomainEventType.java)
- Domain events should hold enough context data about what has happened, but not the whole aggregate root or non-relevant
  data
- Domain events should not be annotated with lombok's `@Setter`, `@Builder`, `@Data`, `@Value` or
  `@AllArgsConstructor`
- Domain events are only raised from aggregate roots by using `AggregateRoot.raiseEvent()` method
- Domain events have the following class level annotations:
    - `@Getter`: for retrieving data (actually getters are quite bad as it violates information hiding principle, but
      for convenience let's keep them)
    - `@TypeAlias(MAINTENANCE_RECORD_CREATED_EVENT)`: use a explict type alias, otherwise the FQCN will be used by
      Spring Data MongoDB which does not survive refactoring of changing package locations. The value should be the same
      as the event's
      `DomainEventType` such as `MAINTENANCE_RECORD_CREATED_EVENT`
    - `@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator)`: for Jackson deserialization as well as
      built from MongoDB, should be `PRIVATE` as it's not supposed to be called manually

Example domain event [MaintenanceRecordCreatedEvent](../src/main/java/com/company/andy/feature/org/maintenance/domain/event/MaintenanceRecordCreatedEvent.java):

```java
@Getter
@TypeAlias("MAINTENANCE_RECORD_CREATED_EVENT")
@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator)
public class MaintenanceRecordCreatedEvent extends DomainEvent {
    private String maintenanceRecordId;
    private String equipmentId;
    private String equipmentName;

    public MaintenanceRecordCreatedEvent(MaintenanceRecord maintenanceRecord, Actor actor) {
        super(MAINTENANCE_RECORD_CREATED_EVENT, maintenanceRecord, actor);
        this.maintenanceRecordId = maintenanceRecord.getId();
        this.equipmentId = maintenanceRecord.getEquipmentId();
        this.equipmentName = maintenanceRecord.getEquipmentName();
    }
}
```

- For every domain event class, you need to register it
  inside [DomainEvent](../src/main/java/com/company/andy/common/event/DomainEvent.java) using `@JsonTypeInfo`. This is
  for Jackson to work properly even without the type information(`__TypeId__`) in Kafka message headers.

```java
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true)
@JsonSubTypes(value = {
    @Type(value = MaintenanceRecordCreatedEvent.class, name = "MAINTENANCE_RECORD_CREATED_EVENT"),
})
```

- A domain event can extend another abstract domain event, this abstract domain event should extend `DomainEvent`. For
  example, `EquipmentNameUpdatedEvent` extends `EquipmentUpdatedEvent` which further extends `DomainEvent`.  You
  should register the concrete `EquipmentNameUpdatedEvent` inside `DomainEvent` but not the abstract
  `EquipmentUpdatedEvent`.

```java
@Getter
@TypeAlias("EQUIPMENT_NAME_UPDATED_EVENT")
@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator)
public class EquipmentNameUpdatedEvent extends EquipmentUpdatedEvent {
    private String updatedName;

    public EquipmentNameUpdatedEvent(String updatedName, Equipment equipment) {
        super(EQUIPMENT_NAME_UPDATED_EVENT, equipment);
        this.updatedName = updatedName;
    }
}
```

```java
@Getter
@NoArgsConstructor(access = PROTECTED)
public abstract class EquipmentUpdatedEvent extends DomainEvent {
    private String equipmentId;

    public EquipmentUpdatedEvent(DomainEventType type, Equipment equipment) {
        super(type, equipment);
        this.equipmentId = equipment.getId();
    }
}
```

### EventHandler

- All even handlers should
  extend [AbstractEventHandler](../src/main/java/com/company/andy/common/event/consume/AbstractEventHandler.java)
- An event can be handled by multiple event handlers, and they operate independently to each other
- You may choose to override `AbstractEventHandler`'s `isIdempotent()`, `isTransactional()` and `priority()` for
  specific purposes, where:
    - `isIdempotent()`: returns `true` if the handler can be run repeatedly without any problem, default value is
      `false`
    - `isTransactional()`: returns `true` if the handler should be atomic, normally it should return `true`, which is
      also the default value. It should return `false` for cases where the handlers does not involve database
      operations, or it handles large amount of database records that exceeds the MongoDB transaction limits.
    - `priority()`: used for multiple handlers with the same event, return the priority of the handler, smaller value
      means higher priority
- Event handlers serve similar purposes as command services in that they both result in data state changes in the
  software, and they both are facade which orchestrate other components(mainly the domain model) but does not contain business logic by
  themselves
- Event handlers can use `ExceptionSwallowRunner` to run multiple independent operations, in which exceptions raised in
  one operation does not affect other operations

Example event handler [EquipmentDeletedEventEventHandler](../src/main/java/com/company/andy/feature/org/equipment/eventhandler/EquipmentDeletedEventEventHandler.java):

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class EquipmentDeletedEventEventHandler extends AbstractEventHandler<EquipmentDeletedEvent> {
    private final DeleteAllMaintenanceRecordsUnderEquipmentTask deleteAllMaintenanceRecordsUnderEquipmentTask;

    @Override
    public void handle(EquipmentDeletedEvent event, Actor actor) {
        ExceptionSwallowRunner.run(() -> deleteAllMaintenanceRecordsUnderEquipmentTask.run(event.getEquipmentId()));
    }
}
```

### Factory

- Factories are used to create aggregate roots
- In factories, before calling aggregate roots's constructors, there usually exists some business validations
- If no business validation is required, the factory can be as simple as just call aggregate roots's constructors, but
  for consistency, let's always use factories to create aggregate roots no matter how simple the factory is
- Use factories to create aggregate roots makes our code more explict as the creation of aggregate roots is an important
  moment in software

Example factory [MaintenanceRecordFactory](../src/main/java/com/company/andy/feature/org/maintenance/domain/MaintenanceRecordFactory.java):

```java
@Component
@RequiredArgsConstructor
public class MaintenanceRecordFactory {

    public MaintenanceRecord create(Equipment equipment,
                                    EquipmentStatus status,
                                    String description,
                                    Actor actor) {
        return new MaintenanceRecord(equipment.getId(), equipment.getName(), status, description, actor);
    }
}
```

### Task

- Tasks represents a standalone operation that usually operate on multiple database rows(documents)
- Tasks are like domain services, but for convenience it can access database directly using `MongoTemplate`
- Tasks are usually called by event handlers
- Tasks should be put under the package where the task operates on, but not where the task is called, for example,
  `SyncEquipmentNameToMaintenanceRecordsTask` should be put under `maintenance.domain.task` package instead of
  `equipment.domain.task` package, even though the task is called by an EventHandler in `equipment.eventhandler`
  package.

Example task [SyncEquipmentNameToMaintenanceRecordsTask](../src/main/java/com/company/andy/feature/org/equipment/domain/task/SyncEquipmentNameToMaintenanceRecordsTask.java):

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class SyncEquipmentNameToMaintenanceRecordsTask {
    private final MongoTemplate mongoTemplate;
    private final EquipmentRepository equipmentRepository;

    public void run(String equipmentId) {
        equipmentRepository.byIdOptional(equipmentId).ifPresent(equipment -> {
            Query query = new Query(where(MaintenanceRecord.Fields.equipmentId).is(equipmentId));
            Update update = new Update().set(MaintenanceRecord.Fields.equipmentName, equipment.getName());
            mongoTemplate.updateMulti(query, update, MaintenanceRecord.class);
            log.info("Synced equipment[{}] name to all maintenance records.", equipment.getId());
        });
    }
}
```

### Job

- A job represents a background operation triggered by a timer
- Jobs are quite similar to tasks, the difference is that a job is relatively heavy weight and addresses a systematic
  problem, yet a task handle a single specific problem

Example job [RemoveOldMaintenanceRecordsJob](../src/main/java/com/company/andy/feature/org/maintenance/job/RemoveOldMaintenanceRecordsJob.java):

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class RemoveOldMaintenanceRecordsJob {
    private static final int KEEP_DAYS = 180;
    private final MongoTemplate mongoTemplate;

    public void run() {
        log.info("Start removing maintenance records that are more than {} days old.", KEEP_DAYS);
        Query query = Query.query(where(AggregateRoot.Fields.createdAt).lt(Instant.now().minus(KEEP_DAYS, DAYS)));
        DeleteResult result = mongoTemplate.remove(query, MaintenanceRecord.class);
        log.info("Removed {} maintenance records that are more than {} days old.", KEEP_DAYS, result.getDeletedCount());
    }
}
```

### QueryService

- Query services and command services both belong to application services
- Query services' only purpose is for querying data
- Query services exist to stand apart from command services, making the query service a separate concern
- Query services follow CQRS principle in that it can access database directly, bypassing the domain model which
  command services operate on
- Query services can have its own data model just for querying data, for
  example [QPagedEquipment](../src/main/java/com/company/andy/feature/org/equipment/query/QPagedEquipment.java) represents
  an equipment item in the list

Example query service [EquipmentQueryService](../src/main/java/com/company/andy/feature/org/equipment/query/EquipmentQueryService.java):

```java
@Component
@RequiredArgsConstructor
public class EquipmentQueryService {
    private final MongoTemplate mongoTemplate;
    private final EquipmentRepository equipmentRepository;

    public PagedResponse<QPagedEquipment> pageEquipments(PageEquipmentsQuery query, Actor actor) {
        Criteria criteria = where(AggregateRoot.Fields.orgId).is(actor.getOrgId());
        
        // code omitted
        
        List<QPagedEquipment> equipments = mongoTemplate.find(query.with(pageable), QPagedEquipment.class, EQUIPMENT_COLLECTION);
        return new PagedResponse<>(equipments, pageable, count);
    }
}
```

### Query

- Query objects are quite similar to command objects, the main difference is that query objects are request objects that
  instructs the software to read data, yet command objects are for writing data
- For queries that return paged data, the query object should
  extend [PageQuery](../src/main/java/com/company/andy/common/utils/PageQuery.java)
- Query objects should use JSR-303 annotations  (such as `@NotNull`, `@Max` and `@Pattern`) for data validation
- For API documentation, `@Schema` should be used to on query fields
- Query objects have the following class level annotations:
    - `@Getter`: for retrieving data
    - `@SuperBuilder`: builder, we don't use `@Builder` but `@SuperBuilder` because the query extends `PageQuery` which
      has its own fields, and `@SuperBuilder` can handle builder for parent class's fields
    - `@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator)`: for Jackson deserialization, should be
      `PRIVATE` as it's not supposed to be called manually

Example query object [PageEquipmentsQuery](../src/main/java/com/company/andy/feature/org/equipment/query/PageEquipmentsQuery.java):

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
