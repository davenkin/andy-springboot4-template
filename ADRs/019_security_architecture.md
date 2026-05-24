- 基于actor，所有的操作方，都抽象成actor
- org操作和system从security侧就分开了，系统管理相关的接口全部以system前缀，其他的接口全部为针对org的
- 系统管理员（拥有system_admin角色）可以访问org接口，但是此时所扮演的actor是org范畴下的actor，而不是系统actor，也即需要指定一个org才能完成认证
  （通过x-org 请求头字段传入所需要扮演的org）
- 并不是所有的方法都需要传入actor，是按需的，但是不用自己创建actor，而是在需要时从源头传下去。
- 方法尽量使用抽象几类Actor，而不是具体的比如OrgActor，当然如果的确需要使用具体类，比如需要获取当前用户的orgId，则很明显应该使用orgactor。
- 列出3种处理流程：org actor，system actor和匿名用户，另外还有job 如何处理，event handler如何处理
- 以后你可能需要重新实现自己的认证流程，但是无论如何actor之后是不会变的，因此从command
  service之后，也即进入真正的业务边界之后，系统将不再依赖springsecuirty，从而做到和认证技术框架的解耦。
- 哪些api时哪些actor能访问的，给出例子