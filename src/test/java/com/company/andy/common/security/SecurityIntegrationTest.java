package com.company.andy.common.security;

import com.company.andy.IntegrationTest;
import com.company.andy.common.exception.ApiError;
import com.company.andy.common.exception.QApiErrorResponse;
import com.company.andy.common.model.actor.SystemActor;
import com.company.andy.common.utils.ResponseId;
import com.company.andy.feature.demoreservation.query.PageDemoReservationQuery;
import com.company.andy.feature.equipment.domain.EquipmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.company.andy.TestFixture.*;
import static com.company.andy.common.exception.ErrorCode.ACCESS_DENIED;
import static com.company.andy.common.exception.ErrorCode.AUTHENTICATION_FAILED;
import static com.company.andy.common.model.OrgRole.ORG_ADMIN;
import static com.company.andy.common.model.OrgRole.ORG_IT_ADMIN;
import static com.company.andy.common.utils.Constants.SYSTEM_ACTOR_ORG_ID_HEADER;
import static com.company.andy.feature.equipment.EquipmentTestFixture.randomCreateEquipmentCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SecurityIntegrationTest extends IntegrationTest {
    @Autowired
    private EquipmentRepository equipmentRepository;

    @Test
    void should_throw_401_error_if_jwt_not_provided_for_org_endpoint() {
        QApiErrorResponse response = restTestClient.post()
                .uri("/equipments")
                .body(randomCreateEquipmentCommand())
                .exchange().expectStatus().isUnauthorized()
                .expectBody(QApiErrorResponse.class).returnResult().getResponseBody();

        ApiError error = response.error();
        assertEquals(401, error.status());
        assertEquals(AUTHENTICATION_FAILED, error.code());
    }

    @Test
    void should_throw_403_error_if_jwt_not_allowed_for_org_endpoint() {
        QApiErrorResponse response = restTestClient.post()
                .uri("/equipments").headers(authHeaderOf(randomHumanUserOrgActor(ORG_IT_ADMIN)))
                .body(randomCreateEquipmentCommand())
                .exchange().expectStatus().isForbidden()
                .expectBody(QApiErrorResponse.class).returnResult().getResponseBody();

        ApiError error = response.error();
        assertEquals(403, error.status());
        assertEquals(ACCESS_DENIED, error.code());
    }

    @Test
    void should_throw_401_error_if_jwt_not_provided_for_system_endpoint() {
        QApiErrorResponse response = restTestClient.post()
                .uri("/system/demo-reservations/paged")
                .body(PageDemoReservationQuery.builder().pageSize(12).build())
                .exchange().expectStatus().isUnauthorized()
                .expectBody(QApiErrorResponse.class).returnResult().getResponseBody();

        ApiError error = response.error();
        assertEquals(401, error.status());
        assertEquals(AUTHENTICATION_FAILED, error.code());
    }

    @Test
    void should_throw_403_error_if_jwt_not_allowed_for_system_endpoint() {
        QApiErrorResponse response = restTestClient.post()
                .uri("/system/demo-reservations/paged").headers(authHeaderOf(randomHumanUserOrgActor(ORG_ADMIN)))
                .body(PageDemoReservationQuery.builder().pageSize(12).build())
                .exchange().expectStatus().isForbidden()
                .expectBody(QApiErrorResponse.class).returnResult().getResponseBody();

        ApiError error = response.error();
        assertEquals(403, error.status());
        assertEquals(ACCESS_DENIED, error.code());
    }

    @Test
    void system_actor_should_impersonate_org_for_org_endpoint() {
        SystemActor actor = randomHumanUserSystemActor();
        String orgId = randomOrgId();

        String equipmentId = restTestClient.post()
                .uri("/equipments")
                .headers(authHeaderOf(actor)).header(SYSTEM_ACTOR_ORG_ID_HEADER, orgId)
                .body(randomCreateEquipmentCommand())
                .exchange().expectStatus().isCreated()
                .expectBody(ResponseId.class).returnResult().getResponseBody().id();

        assertEquals(orgId, equipmentRepository.byId(equipmentId).getOrgId());
    }

    @Test
    void system_actor_should_get_401_error_if_both_jwt_org_id_and_org_id_header_missing_for_org_endpoint() {
        SystemActor actor = randomHumanUserSystemActor();

        restTestClient.post()
                .uri("/equipments")
                .headers(authHeaderOf(actor))
                .body(randomCreateEquipmentCommand())
                .exchange().expectStatus().isUnauthorized();
    }
}
