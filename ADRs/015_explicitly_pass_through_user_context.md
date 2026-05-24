# Explicitly pass through user context

## Context

In software, the "Current User Context" refers to the information about the user who is currently interacting with the
system. This context typically includes details such as the user's identity, roles, permissions, and other relevant
attributes. It helps the system understand who the user is and what they are allowed to do, enabling personalized
experiences and enforcing security measures.

In multithreaded applications, when retrieving the current user context, there are two common approaches:

- Store the user context in a ThreadLocal variable, such as Spring Security's `SecurityContextHolder`. This has the
  advantage of being easily accessible from anywhere in the code without needing to pass it explicitly. However, it
  makes the code implicit and hence increase the cognitive load of the software, also it makes testing harder.
- Pass the user context explicitly as a parameter down to all layers. This approach is more explicit and makes it clear
  where the user context is being used, but it requires more boilerplate code to pass the context through multiple
  layers.

## Decision

We choose to explicitly pass through the user context as a parameter as we prefer explicitness over implicitness. We
believe the boilerplate code will not cost much development time as developers spend most of their time on
designing/thinking/understanding/debugging rather than typing.

## Implementation

[Actor](../src/main/java/com/company/andy/common/model/actor/Actor.java) is used to represent the user context. Is has
three concrete subclasses:

- `OrgActor`: represents an organization actor, it always carries an `orgId`;
- `SystemActor`: represents the system itself, and also can impersonate an `OrgActor`;
- `AnonymousActor`: represents an anonymous actor.

As a general programming rule, your code should use the base class `Actor` as much as possible, only when you need
specific data from a specific type of actor (e.g. `orgId` from `OrgActor`) or need to check for a specific type of
actor (e.g. check if the current actor is `SystemActor`), then you can use the more specific actor type.

Normally you don't need to create these actor object by yourself, the framework will create the appropriate type of actor object
for you and pass it to your code as a parameter. For example:

- In Spring MVC controllers, the actor object is created by Spring Security, you can declare your controller method with
  `@AuthenticationPrincipal` to get the actor object:

```java
    public ResponseId createEquipment(@RequestBody @Valid CreateEquipmentCommand command, @AuthenticationPrincipal OrgActor actor) {
        return new ResponseId(this.equipmentCommandService.createEquipment(command, actor));
    }
```

Normally, for org level resources, the controller method should declare `OrgActor` type, for system level resources, the
controller method should declare `SystemActor` type, and for public resources you should just use `Actor` type. Incorrect declaration of actor type might cause `NullPointerException`.

- In event handlers, a `SystemActor` object is created by the framework and passed to the `handle()` method which you will implement. For example:

```java
    @Override
    protected void handle(EquipmentCreatedEvent event, SystemActor actor) {
        log.info("{} called for Equipment[{}].", this.getClass().getSimpleName(), event.getArId());
    }
```