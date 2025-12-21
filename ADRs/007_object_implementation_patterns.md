# Object implementation patterns

## Context

There are various types of objects in software, such as Controllers, Domain Objects, Factory, Repositories and Services,
etc. Their responsibilities and characteristics differ, but the same type of objects share something in common and it's
important that we keep our coding practices consistent for them.

## Decision

For the same type of objects, we follow the same implementation patterns.

## Implementation

- [Aggregate Root](#aggregate-root)
- [Repository](#repository)
- [Controller](#controller)
- [CommandService](#commandservice)
- [Command](#command)
- [DomainService](#domainservice)
- [Domain Event](#domain-event)
- [EventHandler](#eventhandler')
- [Factory](#factory)
- [Task](#task)
- [Job](#job)
- [QueryService](#queryservice)
- [Query](#query)

### Aggregate Root

- Aggregate Roots are the most important types of objects in your software, they contain your core domain logic, they
  are the sole reason your software exists
- All Aggregate Roots should extend [AggregateRoot](../src/main/java/com/company/andy/common/model/AggregateRoot.java)
  base
  class
- All changes to the internal state of Aggregate Roots should go via the public methods of Aggregate Roots
- Aggregate Root should use meaningful constructors to create itself
- For code consistency, always use Factory to create Aggregate Root, no matter how simple it is
- Aggregate Root should not have builder method because builder method can easily results in invalid object
- Aggregate Root should have a globally unique ID and this ID should be generate by the code but not by database
- Aggregate Root should have meaningful business methods for changing its own state. Every business method should ensure
  the object is always in valid state by applying business rules. Business methods might raise Domain Events after state
  is changed.
- Aggregate Root has the following class level annotations:
    - `@Slf4j`: for log
    - `@Getter`: for retrieving data (actually getters are quite bad as it violates information hiding principle, but
      for convenience let's keep them)
    - `@FieldNameConstants`: for access filed names in situations like accessing MongoDB
    - `@TypeAlias(EQUIPMENT_COLLECTION)`: use a explict type alias, otherwise the FQCN will be used by Spring Data
      MongoDB which does not survive refactorings of changing package locations
    - `@Document(EQUIPMENT_COLLECTION)`: for MongoDB collection
    - `@NoArgsConstructor(access = PRIVATE)`: for Jackson deserialization, should be `PRIVATE` it's not supposed to be
      called manually
- Aggregate Root should not be annotated with `@Setter`, `@Builder` or `@Data`
- Besides Aggregate Root, there can be other types of domain objects in the domain model, such as `EquipmentStatus`

Example [Equipment](../src/test/java/com/company/andy/sample/equipment/domain/Equipment.java):

```java
@Slf4j
@Getter
@FieldNameConstants // For access field names
@TypeAlias(EQUIPMENT_COLLECTION) // Use a explict type alias
@Document(EQUIPMENT_COLLECTION) // Configure MongoDB collection name
@NoArgsConstructor(access = PRIVATE) // For Jackson deserialization
public class Equipment extends AggregateRoot {
    public final static String EQUIPMENT_COLLECTION = "equipment";
    private String name;
    private EquipmentStatus status;
    private String holder;
    private long maintenanceRecordCount;

    public Equipment(String name, Operator operator) { // Explict contructors
        super(newEquipmentId(), operator);
        this.name = name;
        raiseEvent(new EquipmentCreatedEvent(this)); // Raise Domain Event
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

### Repository

- Repository abstracts database interactions for accessing Aggregate Roots
- Every Aggregate Root class has its own Repository class
- Repositories should firstly have an interface class and then a concrete implementation class

Example for Repository
interface [EquipmentRepository](../src/test/java/com/company/andy/sample/equipment/domain/EquipmentRepository.java):

```java
public interface EquipmentRepository {
    void save(Equipment equipment);

    void save(List<Equipment> equipments);

    void delete(Equipment equipment);
    
    // more code omitted
}
```

Example for Repository
implementation [MongoEquipmentRepository](../src/test/java/com/company/andy/sample/equipment/infrastructure/MongoEquipmentRepository.java):

```java
@Repository
@RequiredArgsConstructor
public class MongoEquipmentRepository extends AbstractMongoRepository<Equipment> implements EquipmentRepository {
    private final CachedMongoEquipmentRepository cachedMongoEquipmentRepository;

    @Override
    public List<EquipmentSummary> cachedEquipmentSummaries(String orgId) {
        return cachedMongoEquipmentRepository.cachedEquipmentSummaries(orgId).summaries();
    }
    // more code ommited
}
```

- All Repository implementation should
  extend [AbstractMongoRepository](../src/main/java/com/company/andy/common/infrastructure/AbstractMongoRepository.java)
- For cache, the Repository implementation can reference a cache Repository, the cache Repository also extends
  `AbstractMongoRepository`

Example of cache
Repository [CachedMongoEquipmentRepository](../src/test/java/com/company/andy/sample/equipment/infrastructure/CachedMongoEquipmentRepository.java):

```java
@Slf4j
@Repository
@RequiredArgsConstructor
public class CachedMongoEquipmentRepository extends AbstractMongoRepository<Equipment> {
    private static final String ORG_EQUIPMENT_CACHE = "ORG_EQUIPMENTS";

    @Cacheable(value = ORG_EQUIPMENT_CACHE, key = "#orgId")
    public CachedOrgEquipmentSummaries cachedEquipmentSummaries(String orgId) {
        requireNonBlank(orgId, "orgId must not be blank.");

        Query query = query(where(AggregateRoot.Fields.orgId).is(orgId)).with(by(ASC, createdAt));
        query.fields().include(AggregateRoot.Fields.orgId, Equipment.Fields.name, Equipment.Fields.status);
        return new CachedOrgEquipmentSummaries(mongoTemplate.find(query, EquipmentSummary.class, EQUIPMENT_COLLECTION));
    }
}
```

### Controller

- Controller should be very thin by offloading the work to CommandService for writing data and QueryService for reading
  data
- Controller classes should be annotated with `@Validated` to enable request validation
- Request objects in method parameters should be annotated with `@Valid` to enable request validation
- Controller ensures an [Operator](../src/main/java/com/company/andy/common/model/operator/Operator.java) is
  fetched/created
  from the reqeust and passed to CommandService or QueryService
- Controller should follow REST principles on naming URLs and choosing HTTP methods

Example [EquipmentController](../src/test/java/com/company/andy/sample/equipment/controller/EquipmentController.java):

```java
@Validated // To enable request validation
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/equipments")
public class EquipmentController {
    private final EquipmentCommandService equipmentCommandService;
    private final EquipmentQueryService equipmentQueryService;

    @PostMapping
    public ResponseId createEquipment(@RequestBody @Valid CreateEquipmentCommand command) {
        // In real situations, operator is normally created from the current user in context, such as Spring Security's SecurityContextHolder
        Operator operator = SAMPLE_USER_OPERATOR;

        return new ResponseId(this.equipmentCommandService.createEquipment(command, operator));
    }
}
```

### CommandService

- CommandService serves as the facade for the domain model
- Every public method in CommandService should represent a use case, and should be annotated with `@Transactional` if it
  writes to database
- Methods in CommandService usually accepts a Command object as parameter, as well as an `Operator` object
- CommandService should not contain business logic
- CommandService returns the Aggregate Root's ID for creating objects, and return `void` for updating or deleting
  Aggregate Roots

Example [EquipmentCommandService](../src/test/java/com/company/andy/sample/equipment/command/EquipmentCommandService.java):

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class EquipmentCommandService {
    private final EquipmentRepository equipmentRepository;
    private final EquipmentFactory equipmentFactory;
    private final EquipmentDomainService equipmentDomainService;

    @Transactional
    public String createEquipment(CreateEquipmentCommand command, Operator operator) {
        Equipment equipment = equipmentFactory.create(command.name(), operator);
        equipmentRepository.save(equipment);
        log.info("Created Equipment[{}].", equipment.getId());
        return equipment.getId();
    }
}
```

### Command

- Command objects are request objects that instructs the software to change its data state
- Command should be modeled as Java Record
- Command can be annotated with `@Builder` for testing purpose
- Command object should use JSR-303 annotations  (such as `@NotNull`, `@Max` and `@Pattern`) for data validation

Example [CreateMaintenanceRecordCommand](../src/test/java/com/company/andy/sample/maintenance/command/CreateMaintenanceRecordCommand.java):

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

- DomainService is totally different from CommandService(or QueryService) in that DomainService is part of the domain
  model, but CommandService is the gate to domain model
- DomainService holds domain logic
- Normally we don't want DomainService, as domain logic should best be reside in Aggregate Roots. DomainService is
  our last resort if the business logic is not suitable to be put inside Aggregate Roots.

Example [EquipmentDomainService](../src/test/java/com/company/andy/sample/equipment/domain/EquipmentDomainService.java):

```java
@Component
@RequiredArgsConstructor
public class EquipmentDomainService {
    private final EquipmentRepository equipmentRepository;

    public void updateEquipmentName(Equipment equipment, String newName) {
        if (!Objects.equals(newName, equipment.getName()) &&
            equipmentRepository.existsByName(newName, equipment.getOrgId())) {
            throw new ServiceException(EQUIPMENT_NAME_ALREADY_EXISTS,
                    "Equipment Name Already Exists.",
                    mapOf(AggregateRoot.Fields.id, equipment.getId(), Equipment.Fields.name, newName));
        }

        equipment.updateName(newName);
    }
}
```

In the above example, the business logic of "checking duplicated equipment name" falls outside the ability of
`Equipment`
itself, hence `EquipmentDomainService` is used instead.

### Domain Event

- All Domain Events should extend [DomainEvent](../src/main/java/com/company/andy/common/event/DomainEvent.java)
- Domain Events should be immutable as it represent something already happened which cannot not be changed
- Every Domain Event should register its own type
  inside [DomainEventType](../src/main/java/com/company/andy/common/event/DomainEventType.java)
- Domain Event should hold enough context data about what happened, but not the whole Aggregate Root or non-relevant
  data
- Domain Event should not be annotated with `@Setter`, `@Builder` or  `@Data`
- Domain Events are only raised from Aggregate Roots by using `AggregateRoot.raiseEvent()` method
- Domain Event has the following class level annotations:
    - `@Getter`: for retrieving data (actually getters are quite bad as it violates information hiding principle, but
      for convenience let's keep them)
    - `@TypeAlias(MAINTENANCE_RECORD_CREATED_EVENT)`: use a explict type alias, otherwise the FQCN will be used by
      Spring Data MongoDB which does not survive refactoring of changing package locations. The value should be the same
      as the event's
      `DomainEventType` such as `MAINTENANCE_RECORD_CREATED_EVENT`
    - `@NoArgsConstructor(access = PRIVATE)`: for Jackson deserialization

Example [MaintenanceRecordCreatedEvent](../src/test/java/com/company/andy/sample/maintenance/domain/event/MaintenanceRecordCreatedEvent.java):

```java
@Getter
@TypeAlias("MAINTENANCE_RECORD_CREATED_EVENT")
@NoArgsConstructor(access = PRIVATE)
public class MaintenanceRecordCreatedEvent extends DomainEvent {
    private String maintenanceRecordId;
    private String equipmentId;
    private String equipmentName;

    public MaintenanceRecordCreatedEvent(MaintenanceRecord maintenanceRecord) {
        super(MAINTENANCE_RECORD_CREATED_EVENT, maintenanceRecord);
        this.maintenanceRecordId = maintenanceRecord.getId();
        this.equipmentId = maintenanceRecord.getEquipmentId();
        this.equipmentName = maintenanceRecord.getEquipmentName();
    }
}
```

### EventHandler

- All EventHandlers should
  extend [AbstractEventHandler](../src/main/java/com/company/andy/common/event/consume/AbstractEventHandler.java)
- An event can be handled by multiple EventHandlers, and they operate independently to each other
- You may choose to override `AbstractEventHandler`'s `isIdempotent()`, `isTransactional()` and `priority()` for
  specific purposes, where:
    - `isIdempotent()`: returns `true` if the handler can be run repeatedly without any problem, default value is
      `false`
    - `isTransactional()`: returns `true` if the handler should be atomic, normally it should return `true`, which is
      also
      the default value. It should return `false` for cases where the handlers does not involve database operations, or
      it handles large amount of database records that exceeds the MongoDB transaction limits.
    - `priority()`: used for multiple handlers with the same event, return the priority of the handler, smaller value
      means higher priority
- EventHandler serves a similar purpose as CommandService in that they both result in data state changes in the
  software, and they both are facade which orchestrate other components to work but does not contain business logic by
  themselves
- EventHandler can use `ExceptionSwallowRunner` to run multiple independent operations, in which exceptions raised in
  one operation does not affect other operations

Example [EquipmentDeletedEventEventHandler](../src/test/java/com/company/andy/sample/equipment/eventhandler/EquipmentDeletedEventEventHandler.java):

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class EquipmentDeletedEventEventHandler extends AbstractEventHandler<EquipmentDeletedEvent> {
    private final DeleteAllMaintenanceRecordsUnderEquipmentTask deleteAllMaintenanceRecordsUnderEquipmentTask;
    private final EquipmentRepository equipmentRepository;

    @Override
    public void handle(EquipmentDeletedEvent event) {
        ExceptionSwallowRunner.run(() -> {
            equipmentRepository.evictCachedEquipmentSummaries(event.getArOrgId());
        });

        ExceptionSwallowRunner.run(() -> deleteAllMaintenanceRecordsUnderEquipmentTask.run(event.getEquipmentId()));
    }
}
```

### Factory

- Factory is used to create Aggregate Roots
- In Factories, before calling Aggregate Roots's constructors, there usually exists some business validations
- If no business validation is required, the Factory can be as simple as just call Aggregate Roots's constructors, but
  for consistency, let's always use Factory to create Aggregate Roots no matter how simple the Factory is
- Use Factory to create Aggregate Roots makes our code more explict as the creation of Aggregate Roots is an important
  moment in software

Example [MaintenanceRecordFactory](../src/test/java/com/company/andy/sample/maintenance/domain/MaintenanceRecordFactory.java):

```java
@Component
@RequiredArgsConstructor
public class MaintenanceRecordFactory {

    public MaintenanceRecord create(Equipment equipment,
                                    EquipmentStatus status,
                                    String description,
                                    Operator operator) {
        return new MaintenanceRecord(equipment.getId(), equipment.getName(), status, description, operator);
    }
}
```

### Task

- Tasks represents a standalone operation that usually involves multiple database rows(documents)
- Tasks is like DomainService, but for convenience it can access database directly using `MongoTemplate`
- Tasks are usually called by EventHandlers but not always

Example [SyncEquipmentNameToMaintenanceRecordsTask](../src/test/java/com/company/andy/sample/equipment/domain/task/SyncEquipmentNameToMaintenanceRecordsTask.java):

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

- Job represents a background operation triggered by a timer
- Jobs are quite similar to Tasks, the difference is that Job is relatively heavy weight and addresses a systematic
  problem, yet Tasks handles a single specific problem

Example [RemoveOldMaintenanceRecordsJob](../src/test/java/com/company/andy/sample/maintenance/job/RemoveOldMaintenanceRecordsJob.java):

```java
@Slf4j
@Component
@AllArgsConstructor
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

- QueryService and CommandService both belong to ApplicationService
- QueryService's only purpose is for querying data
- QueryService exists to stand apart from CommandService, making the QueryService a separate concern
- QueryService follows CQRS principle in that it can access database directly, bypassing the Domain Model which
  CommandService relies on
- QueryService can have its own data model just for querying data, for
  example [QPagedEquipment](../src/test/java/com/company/andy/sample/equipment/query/QPagedEquipment.java) represents
  an
  Equipment item in the list

Example [EquipmentQueryService](../src/test/java/com/company/andy/sample/equipment/query/EquipmentQueryService.java):

```java
@Component
@RequiredArgsConstructor
public class EquipmentQueryService {
    private final MongoTemplate mongoTemplate;
    private final EquipmentRepository equipmentRepository;

    public PagedResponse<QPagedEquipment> pageEquipments(PageEquipmentQuery query, Operator operator) {
        Criteria criteria = where(AggregateRoot.Fields.orgId).is(operator.getOrgId());
        
        // code omitted
        
        List<QPagedEquipment> equipments = mongoTemplate.find(query.with(pageable), QPagedEquipment.class, EQUIPMENT_COLLECTION);
        return new PagedResponse<>(equipments, pageable, count);
    }
}
```

### Query

- Query objects are quite similar to Command objects, the main difference is that Query objects are request objects that
  instructs the software to read data, yet Command objects are for writing data
- For queries that return paged data, the query object should
  extend [PageQuery](../src/main/java/com/company/andy/common/util/PageQuery.java)
- Query objects should use JSR-303 annotations  (such as `@NotNull`, `@Max` and `@Pattern`) for data validation
- For API documentation, `@Schema` should be used to on query fields

Example [PageEquipmentQuery](../src/test/java/com/company/andy/sample/equipment/query/PageEquipmentQuery.java):

```java
@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = PRIVATE)
public class PageEquipmentQuery extends PageQuery {
    @Schema(description = "Search text")
    @Max(50)
    private String search;

    @Schema(description = "Equipment status to query")
    private EquipmentStatus status;
}
```
