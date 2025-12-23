# Use DDD

## Context

When developing enterprise applications, there are various architectural styles that can be adopted. Some of the common
styles include:

- CRUD style data manipulating: Use some pure technical frameworks to manipulate data, there is not much business
  modeling in it but just
  CRUD(Create, Read, Update, Delete) operations.
- [Domain Driven Design (DDD)](https://martinfowler.com/bliki/DomainDrivenDesign.html): Focus on the business domains,
  and let code to directly express the domain rules and behaviors,
  aims to decouple the
  domain complexity from technical complexity.

## Decision

We choose **DDD** as the architectural style as it helps us to focus on the business problems first instead of technical
problems, allowing us to write more
maintainable and scalable code. Programming is not just about CRUD or calling APIs, it's about solving problems and
creating business value, and DDD excels on this.

## Implementation

This is an ADR but not a lecture on DDD, so here we only list some common DDD principles:

- Make sure everybody speaks the same language over the business domain, this includes domain experts, product owners,
  UX, DEVs and QAs. Also, the same language should be used in the code. This is
  called [Ubiquitous Language](https://martinfowler.com/bliki/UbiquitousLanguage.html).
- Remember, the sole reason that your software exists is to solve a specific problem of a domain. Here the "domain"
  represents the first letter `D` in `DDD`.
- Make a clear separation between domain code and technical code, this is why we have the concept of `Domain Model`.
- In domain model, the most important concept is **Aggregate Root**s. You may roughly think of them as the major
  business
  entity classes in you code. Aggregate Roots are the major places where your business logic happens.

Example Aggregate Root [Equipment](../src/main/java/com/company/andy/feature/equipment/domain/Equipment.java):

```java
@FieldNameConstants
@TypeAlias(EQUIPMENT_COLLECTION)
@Document(EQUIPMENT_COLLECTION)
@NoArgsConstructor(access = PRIVATE)
public class Equipment extends AggregateRoot {
    public final static String EQUIPMENT_COLLECTION = "equipment";
    private String name;
    private EquipmentStatus status;
    private String holder;
    private long maintenanceRecordCount;
}
```

- Sometimes, the business logic is not suitable for residing in Aggregate Root, so **DomainService**s can be created to
  hold such business logic. But please pay attention that DomainServices are the last place you should resort to, most
  of the time you should put business logic code inside your Aggregate Roots.

Example
DomainService [EquipmentDomainService](../src/main/java/com/company/andy/feature/equipment/domain/EquipmentDomainService.java):

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

- Aggregate Roots represent the business logic, but not the use case, in order to bridge the use case and the business
  logic, DDD introduces **ApplicationService**. ApplicationService orchestrates the process flow from use case
  entrypoint to Aggregate Roots. ApplicationService should not contain business logic.
- Together with **CQRS**, ApplicationService can be further categorised into **CommandService** and **QueryService**.
  CommandService deals with the write side and QueryService handles the read side.

Example
CommandService: [EquipmentCommandService](../src/main/java/com/company/andy/feature/equipment/command/EquipmentCommandService.java):

```java
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

Example
QueryService [EquipmentQueryService](../src/main/java/com/company/andy/feature/equipment/query/EquipmentQueryService.java):

```java
@Component
@RequiredArgsConstructor
public class EquipmentQueryService {
    private final MongoTemplate mongoTemplate;
    private final EquipmentRepository equipmentRepository;

    public PagedResponse<QPagedEquipment> pageEquipments(PageEquipmentsQuery query, Operator operator) {
        Criteria criteria = where(AggregateRoot.Fields.orgId).is(operator.getOrgId());

        // code omitted
    }
}
```

- In DDD, we have both ApplicationService and DomainService and they serve differently purposes. When you are creating
  a service class, you should know which kind of services you are creating.
- When retrieving and persisting Aggregate Roots, use **Repository**. Compared with Data Access Object(DAO),
  Repositories
  have a restriction that only Aggregate Roots can have its Repository, other classes in the domain model should not
  have
  Repository. Also, Repository handles the whole Aggregate Root, but not partially.

Example
Repository [MongoEquipmentRepository](../src/main/java/com/company/andy/feature/equipment/infrastructure/MongoEquipmentRepository.java):

```java
@Repository
@RequiredArgsConstructor
public class MongoEquipmentRepository extends AbstractMongoRepository<Equipment> implements EquipmentRepository {
    private final CachedMongoEquipmentRepository cachedMongoEquipmentRepository;

    @Override
    public List<EquipmentSummary> cachedEquipmentSummaries(String orgId) {
        return cachedMongoEquipmentRepository.cachedEquipmentSummaries(orgId).summaries();
    }

    @Override
    public void evictCachedEquipmentSummaries(String orgId) {
        cachedMongoEquipmentRepository.evictCachedEquipmentSummaries(orgId);
    }
    
    // code omitted
}
```

For more detailed information on DDD you may refer to the following online resources:

- DDD books: https://www.workingsoftware.dev/the-ultimate-list-of-domain-driven-design-books-in-2024/
- DDD learning series: https://docs.mryqr.com/ddd-introduction/
- DDD code practices: https://www.cnblogs.com/davenkin/p/ddd-coding-practices.html
