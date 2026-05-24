todo
- 在业务处理的源头，需要确定是orgactor还是system actor
- 代码中尽量使用基类actor，有以下场景

- [Actor](../src/main/java/com/company/andy/common/model/actor/Actor.java) is used to represent anyone/anything that interacts with the systems, it can be a human user, a background job, or event handler. For security, actors are categorized into three types:
    - `OrgActor`: represents an organization actor, it always carries an `orgId`;
    - `SystemActor`: represents the system itself, and also can impersonate an `OrgActor`;
    - `AnonymousActor`: represents an anonymous actor.
- 尽量使用基类Actor，只有需要特定actor数据（比如OrgActor中的orgId）或者需要检查特定类型时（比如需要检查当前是SystemActor）时，才使用更加specific的actor类型。
- 如果从业务上能够确定一个方法只会被特定类型的actor调用（比如一个方法只能被SystemActor调用），那么这个方法的参数应该直接使用特定类型的actor（比如SystemActor），否则应该使用基类Actor。