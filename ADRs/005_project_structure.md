# Project structure

## Context

When structure software projects, there are mainly 2 approaches:

- Structure by technical layers first: create technical layer packages first, such as `domain`, `service`,
  `infrastructure`, then put
  business entities into these layers.
- Structure by business first: create business packages first, then put technical layers inside these packages.

## Decision

We choose "**business first, technical layers second**" approach as the project structure, which means we organize the
code by business entities([Aggregate Root](https://martinfowler.com/bliki/DDD_Aggregate.html)) first, and then by
technical layers.

This approach is more intuitive and easier to understand, as it allows developers to focus on the business first.
Developers can easily get an overall idea of
what this application
does
by a simple glimpse at the business packages.

## Implementation

When implementing, keep the folder structure as flat as possible. The Aggregate Root is at the highest level under
`business` package, then followed by other
technical layers, use the following structure:

The `1` in `(class:1)` indicates there can be only one class, `(class:N)` for multiple.

- `Aggregate Root`(folder:1)
    - `command`(folder:1)
        - `XxxCommandService`(class:1)
        - `XxxCommand`(class:N)
    - `controller`(folder:1)
        - `XxxController`(class:1)
    - `domain`(folder:1)
        - `Xxx`(class:N)
        - `XxxRepository`(class:1)
        - `XxxFactory`(class:1)
        - `XxxDomainService`(class:N)
        - `event`(folder:1)
        - `task`(folder:1)
            - `XxxTask`
    - `eventhandler`(folder:1)
        - `XxxEventHandler`(class:N)
    - `infrastructure`(folder:1)
        - `MongoXxxRepository`(class:N)
    - `job`(folder:1)
        - `XxxScheduler`(class:N)
        - `XxxJob`(class:N)
    - `query`(folder:1)
        - `XxxQueryService`(class:N)
        - `XxxQuery`(class:N)
        - `QXxx`(class:N)

More detailed explanation:

- `Aggregate Root`(folder:1): The top level package, an Aggregate Root represents a major business entity(e.g.
  `equipment`).
    - `command`(folder:1): For sending commands to the application, "command" represents the "C"
      in [CQRS](https://microservices.io/patterns/data/cqrs.html).
        - `XxxCommandService`(class:1): The
          facade [application service](https://ddd-practitioners.com/home/glossary/application-service/)
          class for commands, should end with "CommandService". Every public method in CommandService represents a
          single use case. CommandServices should NOT contain business logic, they orchestrate other components to
          fulfil use cases.  
          Example: [EquipmentCommandService](../src/test/java/com/company/andy/sample/equipment/command/EquipmentCommandService.java).
        - `XxxCommand`(class:N): Represent a single command, it contains the data that you want to send to the
          application, should end
          with "Command".
          Example: [CreateEquipmentCommand](../src/test/java/com/company/andy/sample/equipment/command/CreateEquipmentCommand.java).
    - `controller`(folder:1): For HTTP controllers
        - `XxxController`(class:1): The controller class should end with "Controller". Controllers should be very thin,
          they call CommandServices or QueryServices upon receiving request.
          Example: [EquipmentController](../src/test/java/com/company/andy/sample/equipment/controller/EquipmentController.java).
    - `domain`(folder:1): Contains all the domain models.
        - `Xxx`(class:N): Domain objects hold business logic, they are why your application exists.
          Example: [Equipment](../src/test/java/com/company/andy/sample/equipment/domain/Equipment.java)
          and [EquipmentStatus](../src/test/java/com/company/andy/sample/equipment/domain/EquipmentStatus.java).
        - `XxxRepository`(class:1): Repositories are for retrieving and persisting Aggregate Roots, should end with "
          Repository",
          it's implementation
          classes are located in `infrastructure` folder. Please be noted that repository is per Aggregate Root, namely
          only
          Aggregate Root can have
          repositories, but not all domain objects.
          Example: [EquipmentRepository](../src/test/java/com/company/andy/sample/equipment/domain/EquipmentRepository.java).
        - `XxxFactory`(class:1): Factory class for creating the Aggregate Roots, should end with "Factory". The creation
          of Aggregate Roots
          should be explicit, so always use factories to create them. Normally the
          factory firstly do some
          business validations and then call Aggregate Root's constructor to create object. Example: `EquipmentFactory`.
        - `XxxDomainService`(class:N): A [domain service](https://ddd-practitioners.com/home/glossary/domain-service/)
          class, like other domain objects, holds business logic. But, it should be your last resort when business logic
          cannot fit into other
          domain objects. Domain services usually end with "DomainService", but you can use other meaningful suffixes as
          well such as "XxxChecker" or "XxxProvider".
          Example: [EquipmentDomainService](../src/test/java/com/company/andy/sample/equipment/domain/EquipmentDomainService.java).
        - `event`(folder:1): This folder contains all the Domain Event classes that are raised by the Aggregate Root.
            - XxxEvent(class:N): Domain event class, should end with "Event", it represents a significant change in
              Aggregate Root. The naming convention
              is `[Aggregate Root Name] + [Passive form of verbs] + Event`.
              Example: [EquipmentCreatedEvent](../src/test/java/com/company/andy/sample/equipment/domain/event/EquipmentCreatedEvent.java).
        - `task`(folder:1): Contains various tasks.
            - `XxxTask`(class:N): A task represents a standalone operation, should end with "Task". Tasks are
              usually called from jobs and event handlers.
              Example: [CountMaintenanceRecordsForEquipmentTask](../src/test/java/com/company/andy/sample/equipment/domain/task/CountMaintenanceRecordsForEquipmentTask.java).
    - `eventhandler`(folder:1): Contains all the event handler classes.
        - `XxxEventHandler`(class:N): Event handler class, should end with "EventHandler". Example:
          [EquipmentCreatedEventHandler](../src/test/java/com/company/andy/sample/equipment/eventhandler/EquipmentCreatedEventHandler.java).
    - `infrastructure`(folder:1): Contains the infrastructure code that is related to the Aggregate Root.
        - `MongoXxxRepository`(class:N): The repository implementations, should end with "Repository".
          Example: [MongoEquipmentRepository](../src/test/java/com/company/andy/sample/equipment/infrastructure/MongoEquipmentRepository.java).
    - `job`(folder:1):Contains background jobs that are related to the Aggregate Root.
        - `XxxScheduler`(class:N): Scheduling configuration, should end with "Scheduler".
          Example: [EquipmentScheduler](../src/test/java/com/company/andy/sample/equipment/job/EquipmentScheduler.java).
        - `XxxJob`(class:N): Represents a background job, should end with "Job". A job might run multiple tasks.
          Example:
          [RemoveOldMaintenanceRecordsJob](../src/test/java/com/company/andy/sample/maintenance/job/RemoveOldMaintenanceRecordsJob.java).
    - `query`(folder:1): For querying data, "query" represents the "Q"
      in [CQRS](https://microservices.io/patterns/data/cqrs.html). Queries can bypass the domain model and hit database
      directly using what ever means that suit you.
        - `XxxQueryService`(class:N): The
          facade [application service](https://ddd-practitioners.com/home/glossary/application-service/) class for
          queries, should end with "QueryService". Compared with a single CommandService in `command` package, you may
          have multiple QueryServices under the `query` package. QueryServices usually hold query logic directly without
          calling other classes.
          Example: [EquipmentQueryService](../src/test/java/com/company/andy/sample/equipment/query/EquipmentQueryService.java).
        - `XxxQuery`(class:N): Request class of a query, should end with "Query".
          Example: [PageEquipmentQuery](../src/test/java/com/company/andy/sample/equipment/query/PageEquipmentQuery.java).
        - `QXxx`(class:N): Response class of a query, should start with the letter "Q".
          Example: [QPagedEquipment](../src/test/java/com/company/andy/sample/equipment/query/QPagedEquipment.java).
