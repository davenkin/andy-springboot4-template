# Testing strategy

## Context

Backend developers usually write both unit tests and integration tests. According
to [Testing Pyramid](https://martinfowler.com/bliki/TestPyramid.html), unit tests constitute the base of the pyramid
while integration tests are at a level higher. The goal is to have a large number of unit tests and a smaller number of
integration tests. But in our case, we find that unit tests can be too fragile and require frequent updates when the
code changes, and it gives us less confidence than integration tests, more detail can be
found [here](https://web.dev/articles/ta-strategies).

## Decision

We choose to focus more on integration tests than unit tests.

We write integration tests for:

- Controller APIs,
  e.g. [EquipmentControllerTest](../src/test/java/com/company/andy/feature/equipment/controller/EquipmentControllerTest.java)
- Event handlers, both internal domain events and external events, e.g. [ExternalMaintenanceRecordCreatedEventHandlerTest](../src/test/java/com/company/andy/feature/maintenance/eventhandler/external/ExternalMaintenanceRecordCreatedEventHandlerTest.java)
- Jobs,
  e.g. [RemoveOldMaintenanceRecordsJobTest](../src/test/java/com/company/andy/feature/maintenance/job/RemoveOldMaintenanceRecordsJobTest.java)

We write unit tests for:

- Aggregate Roots, e.g. [EquipmentTest](../src/test/java/com/company/andy/feature/equipment/domain/EquipmentTest.java)
- Other domain models under `domain` package,
  e.g. [EquipmentDomainServiceTest](../src/test/java/com/company/andy/feature/equipment/domain/EquipmentDomainServiceTest.java)

No need to write tests for:

- CommandService: CommandServices are already covered by controller tests
- QueryService: QueryServices are already covered by controller tests
- Repository: repositories are already covered in integration tests implicitly

## Implementation

- [Integration tests](#integration-tests)
  - [Testing profiles](#testing-profiles) 
  - [Testing Controllers](#testing-controllers)
  - [Test internal DomainEvent handlers](#test-internal-domainevent-handlers)
  - [Test external event handlers](#test-external-event-handlers)
  - [Test Jobs](#test-jobs)
- [Unit tests](#unit-tests)


All tests name should use underscore to separate words, and should be descriptive enough to indicate what the test is
doing, e.g. `should_create_equipment()`.

Junit has been configured to run all test classes and all test methods in parallel as configured in [junit-platform.properties](../src/test/resources/junit-platform.properties). If your test class needs to run all its methods sequentially, you may add `@Execution(ExecutionMode.SAME_THREAD)` to you test class.

Maven's [surefire](https://maven.apache.org/surefire/maven-surefire-plugin/) plugin is used to run both unit tests and integration tests. Even though it's commonly advised that integration tests should be run with [failsafe](https://maven.apache.org/surefire/maven-failsafe-plugin/) plugin, we choose to use `surefire` for both types of tests for simplicity.

### Integration Tests

- All integration tests should extend [IntegrationTest](../src/test/java/com/company/andy/IntegrationTest.java):

```java
@Slf4j
@ActiveProfiles("it")
//@ActiveProfiles("it-local")
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = RANDOM_PORT)
public abstract class IntegrationTest {
}
```

The `IntegrationTest` is annotated with `@SpringBootTest`, meaning the tests will load the whole Spring application
context which uses a real servlet server.

When write your own integration test classes, you only need to extend `IntegrationTest`:

```java
class EquipmentControllerTest extends IntegrationTest {
    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private EquipmentCommandService equipmentCommandService;
    
    @Test
    void should_create_equipment() {
    }
}
```

You can use `@Autowired` to get an instance of the bean that should be tested, or whatever beans that you require to
assist your testing.

When prepare testing data, you can use methods in `CommandService`  or `Repository` to insert/update data into database. Some times you may need to set values on some fields directly, you can use Spring's `ReflectionTestUtils` for such purpose.

Since integration tests write data into database, in order to avoid primary key duplication errors, you
should use random IDs for your objects in every test method, do not reuse IDs across test methods. Also, multiple tests using the same IDs can pollute each other.

In order to [enhance testing performance](https://www.baeldung.com/spring-tests) by avoiding creating Spring testing
context repeatedly, please use as less mock beans(`@MockitoBean` or `@MockitoSpyBean`) as possible in integration tests, instead create your own stub classes.

#### Testing profiles

There are two profiles for integration test: [application-it.yaml](../src/test/resources/application-it.yaml)
and [application-it-local.yaml](../src/test/resources/application-it-local.yaml). Based on your requirements, you can
choose to enable one of them, but not both.

The main difference between `application-it.yaml` and `application-it-local.yaml` is that the former uses embedded
MongoDB and Redis while the latter uses real ones from your local machine.

- `application-it.yaml`: This is the default profile which you use most of the time. This profile should be enabled for
  CI pipelines. This profile enables developers to run integration tests without setting up any middlewares locally like
  MongoDB, Redis or Kafka. It has the following configurations:
    - Use embedded MongoDB server (`de.flapdoodle.embed:de.flapdoodle.embed.mongo.spring4x`)
    - Use embedded Redis server (`com.github.codemonstur:embedded-redis`)
    - MongoDB transactions enabled
    - Mongock disabled
    - Kafka disabled for both publishing and consuming events
    - Job schedulers are disabled
    - RestClients disabled
- `application-it-local.yaml`: This is only for real local MongoDB/Redis servers and should not be enabled for CI
  pipelines, it has the following configurations:
    - Use your local MongoDB server
    - Use your local Redis server
    - MongoDB transactions enabled (same as `application-it.yaml`)
    - Mongock disabled (same as `application-it.yaml`)
    - Kafka disabled for both publishing and consuming events (same as `application-it.yaml`)
    - Job schedulers are disabled (same as `application-it.yaml`)
    - RestClients disabled (same as `application-it.yaml`)

For both profiles:

- As Kafka is disabled, you will need to call `EventConsumer.consumeXxxEvent()` explicitly for testing event consuming.
- As [Transactional Outbox](https://microservices.io/patterns/data/transactional-outbox.html) pattern is used, the
  DomainEvents will firstly be stored into database before publishing, you may use `IntegrationTest.latestEventFor()` to
  verify the existence of domain events.

#### Testing Controllers

1. Prepared data
2. Call API using `RestTestClient`
3. Verify results
4. If domain events are raised, you can verify it using `latestEventFor()`

```java
    @Test
    void should_create_equipment() {
        // Prepare
        OrgActor actor = randomHumanUserOrgActor(ORG_ADMIN);
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();

        // Execute
        ResponseId responseId = restTestClient.post()
                .uri("/equipments").headers(authHeaderOf(actor))
                .body(createEquipmentCommand)
                .exchange().expectStatus().isCreated()
                .expectBody(ResponseId.class).returnResult().getResponseBody();

        // Verify
        String equipmentId = responseId.id();
        Equipment equipment = equipmentRepository.byId(equipmentId);
        assertEquals(createEquipmentCommand.name(), equipment.getName());
        assertEquals(actor.getOrgId(), equipment.getOrgId());

        // Verify domain events
        EquipmentCreatedEvent equipmentCreatedEvent = latestEventFor(equipmentId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        assertEquals(equipmentId, equipmentCreatedEvent.getEquipmentId());
    }
```


#### Test internal DomainEvent handlers

Usually DomainEvent handler testing is covered in Controller test file, as normally domain events are raised from Controller API calling.

1. Prepare data (usually by calling CommandService or Repository or RestTestClient)
2. Execute the EventHandler using `EventConsumer.consumeDomainEvent()`, do not call `EventHandler.handle()` directly as it's not end-to-end testing.
3. Verify results
4. If other domain events are further raised during the event consuming, you will still need to verify it using `latestEventFor()`

```java
    @Test
    void update_equipment_name_should_also_sync_to_maintenance_records() {
        // Prepare
        OrgActor actor = randomHumanUserOrgActor(ORG_ADMIN);
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, actor);
        String maintenanceRecordId = maintenanceRecordCommandService.createMaintenanceRecord(randomCreateMaintenanceRecordCommand(equipmentId),
                actor);

        // Execute
        UpdateEquipmentNameCommand updateEquipmentNameCommand = randomUpdateEquipmentNameCommand();
        restTestClient.put()
                .uri("/equipments/{id}/name", equipmentId).headers(authHeaderOf(actor))
                .body(updateEquipmentNameCommand)
                .exchange().expectStatus().isOk();

        // Verify
        EquipmentNameUpdatedEvent equipmentNameUpdatedEvent = latestEventFor(equipmentId, EQUIPMENT_NAME_UPDATED_EVENT,
                EquipmentNameUpdatedEvent.class);
        
        // Test domain events
        eventConsumer.consumeDomainEvent(equipmentNameUpdatedEvent);
        assertEquals(updateEquipmentNameCommand.name(), maintenanceRecordRepository.byId(maintenanceRecordId).getEquipmentName());
    }
```

#### Test external event handlers

Unlike domain event handler tests which can be covered in controller tests, external event handlers should be tested in their own test files, as they are not triggered by our own controller APIs but by external systems.

1. Prepare data (usually by calling CommandService or Repository)
2. Execute the EventHandler using `EventConsumer.consumeExternalEvent()`, do not call `EventHandler.handle()` directly as it's not end-to-end testing.
3. Verify results
4. If other domain events are further raised during the event consuming, you will still need to verify it using `latestEventFor()`

```java
   @Test
    void external_maintenance_record_created_event_should_be_added() {
        // Prepare
        OrgActor actor = randomHumanUserOrgActor(ORG_ADMIN);
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, actor);

        ExternalMaintenanceRecordCreatedEvent externalEvent = ExternalMaintenanceRecordCreatedEvent.builder()
                .eventId(randomExternalEventId())
                .eventType("MAINTENANCE_RECORD_CREATED")
                .channelRecordId(UUID.randomUUID().toString())
                .equipmentId(equipmentId)
                .equipmentStatus(EquipmentStatus.RUNNING)
                .description("This is a maintenance record from external system")
                .build();

        // Execute
        eventConsumer.consumeExternalEvent(externalEvent);

        // Verify
        Equipment equipment = equipmentRepository.byId(equipmentId);
        MaintenanceRecord record = maintenanceRecordRepository.latestForOptional(equipmentId).get();
        assertEquals(externalEvent.getChannelRecordId(), record.getChannelRecordId());
        assertEquals(EXTERNAL, record.getChannel());
        assertEquals(externalEvent.getEquipmentStatus(), record.getStatus());
        assertEquals(equipment.getName(), record.getEquipmentName());

        // Verify domain event
        MaintenanceRecordCreatedEvent internalEvent = latestEventFor(record.getId(),
                MAINTENANCE_RECORD_CREATED_EVENT,
                MaintenanceRecordCreatedEvent.class);
        assertEquals(equipmentId, internalEvent.getEquipmentId());
    }
```

#### Testing Jobs

1. Prepare data using CommandService or Repository
2. Run the job
3. Verify results

```java
    @Test
    void should_remove_old_maintenance_records() {
        // Prepare
        OrgActor actor = randomHumanUserOrgActor(ORG_ADMIN);
        String equipmentId = equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), actor);
        CreateMaintenanceRecordCommand createMaintenanceRecordCommand = randomCreateMaintenanceRecordCommand(equipmentId);
        String maintenanceRecordId = maintenanceRecordCommandService.createMaintenanceRecord(createMaintenanceRecordCommand, actor);
        String oldMaintenanceRecordId = maintenanceRecordCommandService.createMaintenanceRecord(createMaintenanceRecordCommand, actor);

        Query query = Query.query(where(MONGO_ID).is(oldMaintenanceRecordId));
        Update update = new Update().set(AggregateRoot.Fields.createdAt, Instant.now().minus(500, DAYS));
        mongoTemplate.updateFirst(query, update, MaintenanceRecord.class);

        // Execute
        removeOldMaintenanceRecordsJob.run();

        // Verify
        assertFalse(maintenanceRecordRepository.exists(oldMaintenanceRecordId));
        assertTrue(maintenanceRecordRepository.exists(maintenanceRecordId));
    }
```

### Unit Tests

- For unit tests without mocks, it's quite straight forward:

```java
class EquipmentTest {
  @Test
  void should_create_equipment() {
    Actor actor = randomOrgUserActor();
    Equipment equipment = new Equipment("name", actor);
    assertEquals("name", equipment.getName());
    assertEquals(1, equipment.getEvents().size());
    assertTrue(equipment.getEvents().stream()
        .anyMatch(domainEvent -> domainEvent.getType() == EQUIPMENT_CREATED_EVENT));
  }
}
```

- For unit tests with mocks, use `@Mock` and `@InjectMocks`, together with `@ExtendWith(MockitoExtension.class)`, to
  simplify the mocking setup:

```java
@ExtendWith(MockitoExtension.class)
class EquipmentDomainServiceTest {

    @Mock // @Mock means it's a mocked object
    private EquipmentRepository equipmentRepository;

    @InjectMocks // @InjectMocks means automatically inject other @Mock objects into this object
    private EquipmentDomainService equipmentDomainService;

    @Test
    void should_update_name() {
        Mockito.when(equipmentRepository.existsByName(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
        Equipment equipment = new Equipment("name", randomHumanUserOrgActor(ORG_ADMIN));

        equipmentDomainService.updateEquipmentName(equipment, "newName", randomHumanUserOrgActor(ORG_ADMIN));

        assertEquals("newName", equipment.getName());
    }
}
```


