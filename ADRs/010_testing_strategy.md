# Testing strategy

## Context

Backend developers usually write both unit tests and integration tests. According
to [Testing Pyramid](https://martinfowler.com/bliki/TestPyramid.html), unit tests
constitute the base of the pyramid while integration tests are at a level higher. The goal is to have a large number of
unit tests and a smaller number of integration tests. But in our case, we find that unit tests can be too fragile and
require frequent updates when the code changes, and it gives us less confidence than integration tests, more detail can
be found [here](https://web.dev/articles/ta-strategies).

## Decision

We choose to focus more on integration tests than unit tests.

We write integration tests for:

- CommandService,
  e.g. [EquipmentCommandServiceIntegrationTest](../src/test/java/com/company/andy/feature/equipment/command/EquipmentCommandServiceIntegrationTest.java)
- QueryService,
  e.g. [EquipmentQueryServiceIntegrationTest](../src/test/java/com/company/andy/feature/equipment/query/EquipmentQueryServiceIntegrationTest.java)
- EventHandler,
  e.g. [EquipmentDeletedEventEventHandlerIntegrationTest](../src/test/java/com/company/andy/feature/equipment/eventhandler/EquipmentDeletedEventEventHandlerIntegrationTest.java)
- Job,
  e.g. [RemoveOldMaintenanceRecordsJobIntegrationTest](../src/test/java/com/company/andy/feature/maintenance/job/RemoveOldMaintenanceRecordsJobIntegrationTest.java)

We write unit tests for:

- Aggregate Roots, e.g. [EquipmentTest](../src/test/java/com/company/andy/feature/equipment/domain/EquipmentTest.java)
- Other domain models under `domain` package,
  e.g. [EquipmentDomainServiceTest](../src/test/java/com/company/andy/feature/equipment/domain/EquipmentDomainServiceTest.java)
- Actually these objects are already covered in integration tests, but integration tests can be quite heavy, so the plan
  is to let integration tests cover the main flow and unit tests cover other corner cases

No need to write tests for:

- Controller: controllers are very thin but requires a heavy set up for testing
- Repository: repositories are usually already covered in integration tests implicitly

## Implementation

### Integration Tests

- All integration tests should extend [IntegrationTest](../src/test/java/com/company/andy/IntegrationTest.java):

```java
@Slf4j
@ActiveProfiles("it")
//@ActiveProfiles("it-local")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public abstract class IntegrationTest {
  private static RedisServer redisServer;
}
```

The `IntegrationTest` is annotated with `@SpringBootTest`, meaning the tests will load the whole Spring application
context which uses a real servlet server.

When write your own integration test classes, you only need to extend `IntegrationTest`:

```java
class EquipmentCommandServiceIntegrationTest extends IntegrationTest {
  @Autowired
  private EquipmentCommandService equipmentCommandService;

  @Autowired
  private EquipmentRepository equipmentRepository;

  @Test
  void should_create_equipment() {}
}
```

You can use `@Autowired` to get an instance of the bean that should be tested, or whatever beans that you require to
assist you testing.

When prepare testing data, try use CommandService's methods to insert data into database as it mimics the real use case,
do not use Repository directly to insert data into database.

Since integration tests write data into database, in order avoid errors like database primary key duplication, you
should use random IDs for your entities and domain events for every test method, do not reused IDs across test methods.
You can use `RandomTestUtils` for getting various random test fixtures and also you can add more to it.

In order to [enhance testing performance](https://www.baeldung.com/spring-tests) by avoiding creating Spring testing
context repeatedly,
please use as less mocks(`@MockitoBean` or `@MockitoSpyBean`) as possible in integration tests.

#### Testing profiles

There are two profiles for integration test: [application-it.yaml](../src/test/resources/application-it.yaml)
and [application-it-local.yaml](../src/test/resources/application-it-local.yaml). Based on your requirements, you can
choose to enable one
of them, but not both.

The main difference between `application-it.yaml` and `application-it-local.yaml` is that the former uses embedded
MongoDB and Redis while
the latter uses real ones from your local machine.

- `application-it.yaml`: This is the default profile and should be enabled for Jenkins CI. This profile enables
  developers to run
  integration tests without setting up any middlewares locally like MongoDB, Redis or Kafka. You can just pull the code
  and run the tests.
  It has the following configurations:
    - Use embedded MongoDB server (`de.flapdoodle.embed:de.flapdoodle.embed.mongo.spring4x`)
    - Use embedded Redis server (`com.github.codemonstur:embedded-redis`)
    - Transaction disabled as flapdoodle reports error for concurrent transactions
    - Mongock disabled
    - Kafka disabled for both sending and consuming events
    - Job schedulers are disabled
- `application-it-local.yaml`: This is only for your local testing and should not be enabled for Jenkins CI, it has the
  following
  configurations:
    - Use your local MongoDB serve
    - Use your local Redis server
    - Mongock disabled (same as `application-it.yaml`)
    - Kafka disabled for both sending and consuming events (same as `application-it.yaml`)
    - Job schedulers are disabled (same as `application-it.yaml`)

For both profiles:

- As Kafka is disabled, you will need to call EventHandlers' `handle()` methods explicitly to ensure the processing of
  events.
- As [Transactional Outbox](https://microservices.io/patterns/data/transactional-outbox.html) pattern is used, the
  Domain Events will firstly be stored into database and then publish, you may use `IntegrationTest.latestEventFor()` to
  verify the existence of domain events:

#### Testing CommandService

1. Prepared command
2. Execute the method your want to test on CommandService
3. Verify results
4. If the method raises domain events, you can verify is raised using `latestEventFor()`

```java
@Test
void should_create_equipment() {
        //Prepare data
        Operator operator = randomUserOperator();
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();

        //Execute 
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, operator);

        //Verify results
        Equipment equipment = equipmentRepository.byId(equipmentId);
        assertEquals(createEquipmentCommand.name(), equipment.getName());
        assertEquals(operator.getOrgId(), equipment.getOrgId());

        // Verify domain events
        // Only need to check the existence of domain event in database,
        // no need to further test event handler as that will be handled in event handlers' own tests
        EquipmentCreatedEvent equipmentCreatedEvent = latestEventFor(equipmentId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        assertEquals(equipmentId, equipmentCreatedEvent.getEquipmentId());
    }
```

#### Testing QueryService

1. Prepare data using CommandService
2. Execute the method your want to test on QueryService
3. Verify results

```java
    @Test
    void should_page_equipments() {
        //Prepare data
        Operator operator = randomUserOperator();
        IntStream.range(0, 20).forEach(i -> {
            equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), operator);
        });

        // Fetch data
        PageEquipmentsQuery query = PageEquipmentsQuery.builder().pageSize(12).build();
        PagedResponse<QPagedEquipment> equipments = equipmentQueryService.pageEquipments(query, operator);

        // Verify results
        assertEquals(12, equipments.getContent().size());
    }

```

#### Testing EventHandler

1. Prepare data using CommandService
2. Execute the EventHandler
3. Verify results
4. If the method raises domain events, you can verify is raised using `latestEventFor()`

```java
    @Test
    void delete_equipment_should_also_delete_all_its_maintenance_records() {
        // Prepare data
        Operator operator = randomUserOperator();
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, operator);
        CreateMaintenanceRecordCommand createMaintenanceRecordCommand = randomCreateMaintenanceRecordCommand(equipmentId);
        String maintenanceRecordId = maintenanceRecordCommandService.createMaintenanceRecord(createMaintenanceRecordCommand, operator);
        assertTrue(maintenanceRecordRepository.exists(maintenanceRecordId));
        
        // Run event handler
        equipmentCommandService.deleteEquipment(equipmentId, operator);
        EquipmentDeletedEvent equipmentDeletedEvent = latestEventFor(equipmentId, EQUIPMENT_DELETED_EVENT, EquipmentDeletedEvent.class);
        equipmentDeletedEventEventHandler.handle(equipmentDeletedEvent);
        
        // Verify results
        assertFalse(maintenanceRecordRepository.exists(maintenanceRecordId));
    }
```

#### Testing Job

1. Prepare data using CommandService
2. Run the job
3. Verify results

```java
    @Test
    void should_remove_old_maintenance_records() {
        // Prepare data
        Operator operator = randomUserOperator();
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, operator);

        CreateMaintenanceRecordCommand createMaintenanceRecordCommand = randomCreateMaintenanceRecordCommand(equipmentId);
        String maintenanceRecordId = maintenanceRecordCommandService.createMaintenanceRecord(createMaintenanceRecordCommand, operator);
        String oldMaintenanceRecordId = maintenanceRecordCommandService.createMaintenanceRecord(createMaintenanceRecordCommand, operator);

        Query query = Query.query(where(MONGO_ID).is(oldMaintenanceRecordId));
        Update update = new Update().set(AggregateRoot.Fields.createdAt, Instant.now().minus(500, DAYS));
        mongoTemplate.updateFirst(query, update, MaintenanceRecord.class);

        // Run the job
        removeOldMaintenanceRecordsJob.run();

        // Verify results
        assertFalse(maintenanceRecordRepository.exists(oldMaintenanceRecordId));
        assertTrue(maintenanceRecordRepository.exists(maintenanceRecordId));
    }
```

### Unit Tests

- For unit tests without mocks, it's quite straight forward.

```java
class EquipmentTest {
  @Test
  void shouldCreateEquipment() {
    Operator operator = RandomTestUtils.randomUserOperator();
    Equipment equipment = new Equipment("name", operator);
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

  @Mock
  private EquipmentRepository equipmentRepository;

  @InjectMocks
  private EquipmentDomainService equipmentDomainService;

  @Test
  void shouldUpdateName() {
    Mockito.when(equipmentRepository.existsByName(Mockito.anyString(), Mockito.anyString())).thenReturn(false);
    Equipment equipment = new Equipment("name", randomUserOperator());

    equipmentDomainService.updateEquipmentName(equipment, "newName");

    assertEquals("newName", equipment.getName());
  }
}
```


