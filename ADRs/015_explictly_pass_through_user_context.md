todo
- 在业务处理的源头，需要确定是orgactor还是system actor
- 代码中尽量使用基类actor，有以下场景

- [Actor](../src/main/java/com/company/andy/common/model/actor/Actor.java) is used to represent anyone/anything that interacts with the systems, it can be a human user, a background job, or event handler. For security, actors are categorized into three types:
    - `OrgActor`: represents an organization actor, it always carries an `orgId`;
    - `SystemActor`: represents the system itself, and also can impersonate an `OrgActor`;
    - `AnonymousActor`: represents an anonymous actor.