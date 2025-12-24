## Introduction

This is a template Spring Boot 4 project with the following features:

- Data persistence using MongoDB
- Messaging using Kafka
- Cache using Redis
- API documentation using [Springdoc](./ADRs/011_api_documentation.md)
- Data migration using [Mongock](https://mongock.io/)
- Architecture validation using [ArchUnit](https://www.archunit.org/)
- Distributed lock for scheduled jobs using [Shedlock](https://github.com/lukas-krecan/ShedLock)
- Standardized [folder structure](./ADRs/005_project_structure.md) with business first approach
- Standardized pagination implementation
  with [PageQuery](src/main/java/com/company/andy/common/util/PageQuery.java)
  and [PagedResponse](src/main/java/com/company/andy/common/util/PagedResponse.java)
- Builtin [Snowflake ID generator](src/main/java/com/company/andy/common/util/SnowflakeIdGenerator.java)
- Domain event modeling based on [DomainEvent](src/main/java/com/company/andy/common/event/DomainEvent.java)
- [Domain event publishing](./ADRs/008_domain_event_publishing.md)
  using [Transactional Outbox](https://microservices.io/patterns/data/transactional-outbox.html) pattern
- [Event consuming](./ADRs/009_event_consuming.md) mechanism with idempotency support
- Standardized [exception handling](./ADRs/012_exception_handling.md)
- Lightweight [Command Query Responsibility Segregation (CQRS)](./ADRs/004_use_cqrs.md) implementation
- Domain modeling using [Domain Driven Design (DDD)](./ADRs/003_use_ddd.md)
- Standardized [request process flow](./ADRs/006_request_process_flow.md)
- Standardized [object implementation pattern](./ADRs/007_object_implementation_patterns.md)

## Tech stack

- Java 17
- Spring Boot 4.0.1
- Spring Data Mongodb
- Spring Data Redis
- Spring Kafka

## How to run locally

- First run `./start-docker-compose.sh` to start the following infrastructures:
    - `MongoDB`: localhost:27125
    - `Kafka`: localhost:9125
    - `Kafka UI`: [http://localhost:8125](http://localhost:8125)
    - `Keycloak`: [http://localhost:7125](http://localhost:7125)
    - `Redis`: localhost:6125
- Run the application in one of the following ways:
    - `./run-local.sh`: this starts the application with debug port on 5005, assuming that docker-compose is already
      up running.
    - `./clear-and-run-local.sh`: this starts the application with debug port on 5005, it also automatically starts
      docker-compose by first removing existing containers and their data if any.
    - Run `main` in  `SpringBootWebApplication`, assuming that docker-compose is already up running.
- Open [http://localhost:5125/about](http://localhost:5125/about) to check if the application runs
  successfully.
- Swagger UI: [http://localhost:5125/swagger-ui/index.html](http://localhost:5125/swagger-ui/index.html)
- To stop docker-compose and delete volume data, run `./stop-docker-compose.sh`.
- If you need integrate with Keycloak, open [http://localhost:7125](http://localhost:7125) for Keycloak management.

## How to build

- Run `./build.sh` to build the project locally.

## How to run tests

- We do both integration testing and unit testing with a preference on integration testing
- To run tests, locate them inside IDE and run them directly from there.
- We have a [testing strategy](./ADRs/010_testing_strategy.md), please read it before writing any tests
- There is no need to start docker-compose for running integration tests, as they do not use dockerized MongoDB or Redis
  but their embedded versions.

## Architecture Decision Records (ADRs)

This project uses [Architecture Decision Records (ADRs)](https://adr.github.io/) to document important architectural
decisions. ADRs are stored in the `ADRs` directory and follow a [specific format](ADRs/000_what_is_adr.md).

## Sample implementation code

The `src/main/java/com/company/andy/feature/equipment` and `src/main/java/com/company/andy/feature/maintenance` folder
contain various common coding practices that should be followed
when writing your own code.

- There are 2 main business entities:
    - `Equipment`: Represents an equipment that needs to be managed, such as a computer.
    - `MaintenanceRecord`: Represents a maintenance record created for an `Equipment`.
- The business stories includes:
    - Create an `Equipment`.
    - Update the `name` of an `Equipment`. The updated name should also be reflected in all `MaintenanceRecord`s for
      this
      `Equipment`,this is achieved using domain event.
    - Update the `holder` of an `Equipment`.
    - Delete an `Equipment`. This should also delete all `MaintenanceRecord`s for this `Equipment`, this is achieved
      using domain event.
    - Create a `MaintenanceRecord`. Its `status` will be used to update the `status` of the `Equipment`, this
      is achieved using domain event.
- It is recommended that you keep these sample code as references until you have implemented at least two real business
  entities.

## Top level business entities (Aggregate Roots)

| Entity Name       | Chinese Name | Abbreviation | Description                                                                                                                           |
|-------------------|--------------|--------------|---------------------------------------------------------------------------------------------------------------------------------------|
| Equipment         | 装备           |              | Sample top level business entity that serves as a reference for consistent coding practice. An Equipment has many MaintenanceRecords. |
| MaintenanceRecord | 装备维护记录       |              | Another sample top level business entity. Multiple MaintenanceRecords can be created for a single  Equipment.                         |

