# Split APIs into multiple planes

### Two API planes
// todo: Three API planes, platform's controller prefix
In multitenant applications, normally there are two API planes:

- The **org API plane** for manipulating org level resources, requires an `OrgActor` to access with the following cases:
    - An org member
    - An org service client
    - An supervisor acting as an `OrgActor` for a specific org
    - A platform service client acting as an `OrgActor` for a specific org
- The **platform API plane** for manipulating platform level resources, requires a `PlatformActor` to access with the
  following cases:
    - A supervisor
    - A platform service client
