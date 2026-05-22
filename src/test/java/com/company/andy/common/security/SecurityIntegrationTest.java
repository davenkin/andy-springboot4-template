package com.company.andy.common.security;

import static com.company.andy.TestFixture.randomHumanUserOrgActor;
import static com.company.andy.common.exception.ErrorCode.ACCESS_DENIED;
import static com.company.andy.common.exception.ErrorCode.AUTHENTICATION_FAILED;
import static com.company.andy.common.model.OrgRole.ORG_IT_ADMIN;
import static com.company.andy.feature.org.equipment.EquipmentTestFixture.randomCreateEquipmentCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.company.andy.IntegrationTest;
import com.company.andy.common.exception.Error;
import com.company.andy.common.exception.QErrorResponse;
import com.company.andy.feature.demoreservation.query.PageDemoReservationQuery;
import org.junit.jupiter.api.Test;

public class SecurityIntegrationTest extends IntegrationTest {
  @Test
  void should_throw_401_error_if_jwt_not_provided_for_org_endpoint() {
    QErrorResponse response = restTestClient.post()
        .uri("/equipments")
        .body(randomCreateEquipmentCommand())
        .exchange().expectStatus().isUnauthorized()
        .expectBody(QErrorResponse.class).returnResult().getResponseBody();

    Error error = response.error();
    assertEquals(401, error.status());
    assertEquals(AUTHENTICATION_FAILED, error.code());
  }

  @Test
  void should_throw_403_error_if_jwt_not_allowed_for_org_endpoint() {
    QErrorResponse response = restTestClient.post()
        .uri("/equipments").headers(authHeaderOf(randomHumanUserOrgActor(ORG_IT_ADMIN)))
        .body(randomCreateEquipmentCommand())
        .exchange().expectStatus().isForbidden()
        .expectBody(QErrorResponse.class).returnResult().getResponseBody();

    Error error = response.error();
    assertEquals(403, error.status());
    assertEquals(ACCESS_DENIED, error.code());
  }

  @Test
  void should_throw_401_error_if_jwt_not_provided_for_system_endpoint() {
    QErrorResponse response = restTestClient.post()
        .uri("/system/demo-reservations/paged")
        .body(PageDemoReservationQuery.builder().pageSize(12).build())
        .exchange().expectStatus().isUnauthorized()
        .expectBody(QErrorResponse.class).returnResult().getResponseBody();

    Error error = response.error();
    assertEquals(401, error.status());
    assertEquals(AUTHENTICATION_FAILED, error.code());
  }

  @Test
  void should_throw_403_error_if_jwt_not_allowed_for_system_endpoint() {
    QErrorResponse response = restTestClient.post()
        .uri("/system/demo-reservations/paged").headers(authHeaderOf(randomHumanUserOrgActor()))
        .body(PageDemoReservationQuery.builder().pageSize(12).build())
        .exchange().expectStatus().isForbidden()
        .expectBody(QErrorResponse.class).returnResult().getResponseBody();

    Error error = response.error();
    assertEquals(403, error.status());
    assertEquals(ACCESS_DENIED, error.code());
  }
}
