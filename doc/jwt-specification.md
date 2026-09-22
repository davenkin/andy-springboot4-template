jwt 中应该包含：
- iss: 颁发者，通常是系统的唯一标识
- sub: 主题，通常是用户的唯一标识
- org_id:表示所属组织ID
- principal_type: 表示用户的类型，可能的值包括：
  - MEMBER: Org的一个成员
  - SUPERVISOR：平台管理员
  - ORG_SERVICE_CLIENT：代表Org的service client，每个org都对应一个service client
  - PLATFORM_SERVICE_CLIENT：平台的service client，每个微服务都对应一个service client