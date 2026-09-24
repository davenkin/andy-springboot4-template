# How to export Keycloak data

Sometimes you may want to export your local Keycloak data for future import, use the following steps:

1. Start Keycloak docker server using `start-docker-compose.sh` and configure Keycloak as per your need.
2. Stop the Keycloak docker server using `docker compose down`, this command stops the Keycloak server but keeps its
   data volumes. This needs to be done before exporting because Keycloak will not allow you to export data while it is
   running, as Keycloak's local database allows only one connection.
3. Run the following command to export the `platform` realm data:

```bash
docker run --rm \
-v andy-springboot4-template_andy-springboot4-template-keycloak-volume:/opt/keycloak/data \
-v $(pwd)/keycloak-export:/tmp/export \
quay.io/keycloak/keycloak:26.2.5 \
export \
--dir /tmp/export \
--realm platform \
--users realm_file
```

4. Run the following command to export the `org` realm data:

```bash
docker run --rm \
-v andy-springboot4-template_andy-springboot4-template-keycloak-volume:/opt/keycloak/data \
-v $(pwd)/keycloak-export:/tmp/export \
quay.io/keycloak/keycloak:26.2.5 \
export \
--dir /tmp/export \
--realm org \
--users realm_file
```

5. Data will be exported to the `keycloak-export` folder.