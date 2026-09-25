This `keycloak-export` folder is used for local Keycloak export/import. It contains the following settings:

- Master realm admin user for managing the Keycloak server: `admin`(password:`admin`);
- A realm named `platform` for hosting platform level users and service clients, it contains:
    - A client named `test-platform-web-portal` for supervisors to login the platform web portal;
    - A user named `test-supervisor`(password:`111111`) for simulating a supervisor user, it has the following claim
      fields:
        - `principal_type = SUPERVISOR`;
        - `supervisor_id = SUP1234567890`;
    - A client (Client ID: `test-platform-001-service-client`, Client Secret:`todo`) for simulating a platform
      level service client, it has the following claim fields:
        - `principal_type = PLATFORM_SERVICE_CLIENT`;
- A realm named `org` for hosting org level users and service clients, it contains:
    - A client named `test-org-web-portal` for org members to login the org web portal;
    - A user named `test-org-admin`(password:`111111`) for simulating an org admin member, it has the following claim
      fields via `org_member_context` client scope:
        - `principal_type = MEMBER`
        - `org_id = 123456`
        - `member_id = MBR1234567890`
    - A client (Client ID: `test-org-service-client-123456`, Client Secret:`kyt2TLc5VbHXeiz5MSpvW36oKIx3yGHc`) for
      simulating an org level service client, it has the following claim fields via
      `test-org-service-client-123456-dedicated` client scope:
        - `principal_type = ORG_SERVICE_CLIENT`
        - `org_id = 123456`
