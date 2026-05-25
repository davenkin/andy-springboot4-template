## Introduction

This is a template Spring Boot 4 project with the following features:

- Data persistence using MongoDB
- Messaging using Kafka
- Caching using Redis
- API documentation using [Springdoc](./ADRs/011_api_documentation.md)
- Data migration using [Mongock](https://mongock.io/)
- Architecture validation using [ArchUnit](https://www.archunit.org/)
- Distributed lock for scheduled jobs using [Shedlock](https://github.com/lukas-krecan/ShedLock)
- Standardized [folder structure](./ADRs/005_project_structure.md) with business first approach
- Standardized pagination implementation with [PageQuery](src/main/java/com/company/andy/common/utils/PageQuery.java)
  and [PagedResponse](src/main/java/com/company/andy/common/utils/PagedResponse.java)
- Builtin [Snowflake ID generator](src/main/java/com/company/andy/common/utils/SnowflakeIdGenerator.java)
- Domain event modeling based on [DomainEvent](src/main/java/com/company/andy/common/event/DomainEvent.java)
- [Domain event publishing](./ADRs/008_domain_event_publishing.md)
  using [Transactional Outbox](https://microservices.io/patterns/data/transactional-outbox.html) pattern
- [Event consuming](./ADRs/009_event_consuming.md) mechanism with idempotency support
- Standardized [exception handling](./ADRs/012_exception_handling.md)
- Lightweight [Command Query Responsibility Segregation (CQRS)](./ADRs/004_use_lightweight_cqrs.md) implementation
- Domain modeling using [Domain Driven Design (DDD)](./ADRs/003_use_ddd.md)
- Standardized [request process flow](./ADRs/006_request_process_flow.md)
- Standardized [object implementation pattern](./ADRs/007_object_implementation_patterns.md)
- Distributed tracing with [Micrometer tracing](https://docs.micrometer.io/tracing/reference/)
  and [OpenTelemetry](https://spring.io/blog/2025/11/18/opentelemetry-with-spring-boot)
- [RestClient](src/main/java/com/company/andy/common/configuration/RestClientConfiguration.java) for making external API calls

## Tech stack

- Java 25
- Spring Boot 4.0.1
- Spring Data Mongodb
- Spring Data Redis
- Spring Kafka

## How to run locally

- First run `./start-docker-compose.sh` to start the following middlewares using Docker:
    - `MongoDB`: localhost:27125
    - `Kafka`: localhost:9125
    - `Kafka UI`: [http://localhost:8125](http://localhost:8125)
    - `Keycloak`: [http://localhost:7125](http://localhost:7125), with the following default settings:
        - Keycloak Admin user for managing Keycloak server:
            - Username: `admin`
            - Password:`admin`
        - Org IT admin:
            - Realm: `test-realm`
            - Client: `test-client`
            - Username: `test-org-it-admin`
            - Password: `11111111`
            - Role: `org_it_admin`
        - Org admin:
            - Realm: `test-realm`
            - Client: `test-client`
            - Username: `test-org-admin`
            - Password: `11111111`
            - Role: `org_admin`
        - System admin:
            - Realm: `test-realm`
            - Client: `test-client`
            - Username: `test-system-admin`
            - Password: `11111111`
            - Role: `system_admin`
        - A claim field named `org_id` with hardcoded value of `12345678` is added to the access token to simulate an org.
    - `Redis`: localhost:6125
- Run the application locally in one of the following ways:
    - `./run-local.sh`: this starts the application with debug port on 5005, assuming that docker-compose is already up
      running.
    - `./clear-and-run-local.sh`: this starts the application with debug port on 5005, it also automatically starts
      docker-compose by first removing existing containers and their data if any.
    - Run `main()` in  `SpringBootWebApplication`, assuming that docker-compose is already up running.
    - By default, the `local` profile(`application-local.yaml`) is used for all the above methods.
- Open [http://localhost:5125/about](http://localhost:5125/about) to check if the application runs successfully.
- Swagger UI: [http://localhost:5125/swagger-ui/index.html](http://localhost:5125/swagger-ui/index.html)
- To stop docker-compose and delete volume data, run `./stop-docker-compose.sh`.

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
decisions. ADRs are stored in the `ADRs` directory and follow a [specific format](ADRs/000_what_is_adr.md). You should go through all the ADRs before you start implementing any code, as they contain important information about the architecture and coding practices of this project.

## Sample implementation code

There are four sample Aggregate Roots which serve as reference implementations:
- [Equipment](src/main/java/com/company/andy/feature/equipment/domain/Equipment.java): Represents an equipment that needs to be managed under an org, such as a computer. 
- [MaintenanceRecord](src/main/java/com/company/andy/feature/maintenance/domain/MaintenanceRecord.java): Represents a maintenance record created for an `Equipment`, it's also an org level object.
- [SystemSettings](src/main/java/com/company/andy/feature/systemsettings/domain/SystemSettings.java): Represents an system level object that are not related to any org and should only be accessed by system admins.
- [DemoReservation](src/main/java/com/company/andy/feature/demoreservation/domain/DemoReservation.java): Represents that a public user has requested a demo of the product.  

The APIs for these sample Aggregate Roots are only exposed in local and testing environment. You may keep them in your real project as implementation references. If you choose to delete them, make sure you also update the ADRs that reference them.