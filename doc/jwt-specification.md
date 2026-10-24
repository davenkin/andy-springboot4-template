# JWT Specification

Please modify this specification according to your project requirements.

### Org API plane

| 字段名                  | 含义                                                     | 必填  |
|----------------------|--------------------------------------------------------|-----|
| `iss`                | Token颁发者的URL，系统通过iss来判定所属的API plane                    | 必填  |
| `org_id`             | 所属org的ID                                               | 必填  |
| `actor_type`         | Actor类型，可取值`MEMBER`，`ORG_SERVICE_CLIENT`               | 必填  |
| `member_id`          | 仅当`actor_type`=`MEMBER`时必填，表示Member ID                 | 非必填 |
| `preferred_username` | 仅当`actor_type`=`MEMBER`时必填，表示member名称                  | 非必填 |
| `azp`                | 仅当    `actor_type`=`ORG_SERVICE_CLIENT`时必填，表示Client ID | 非必填 |

### Platform API plane

| 字段名                  | 含义                                                          | 必填  |
|----------------------|-------------------------------------------------------------|-----|
| `iss`                | Token颁发者的URL，系统通过iss来判定所属的API plane                         | 必填  |
| `actor_type`         | Actor类型，可取值`SUPERVISOR`，`PLATFORM_SERVICE_CLIENT`           | 必填  |
| `supervisor_id`      | 仅针对`actor_type`=`SUPERVISOR`必填，表示Supervisor ID              | 非必填 |
| `preferred_username` | 仅当`actor_type`=`SUPERVISOR`时必填，表示supervisor名称               | 非必填 |
| `azp`                | 仅当    `actor_type`=`PLATFORM_SERVICE_CLIENT`时必填，表示Client ID | 非必填 |

### Device API plane

| 字段名          | 含义                                  | 必填 |
|--------------|-------------------------------------|----|
| `iss`        | Token颁发者的URL，系统通过iss来判定所属的API plane | 必填 |
| `actor_type` | Actor类型，固定取值`DEVICE`                | 必填 |
| `device_id`  | 表示Device ID                         | 必填 |
