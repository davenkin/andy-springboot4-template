package com.company.andy.common.security;

import com.company.andy.IntegrationTest;
import com.company.andy.common.exception.Error;
import com.company.andy.common.exception.QErrorResponse;
import org.junit.jupiter.api.Test;

import static com.company.andy.common.exception.ErrorCode.AUTHENTICATION_FAILED;
import static com.company.andy.feature.org.equipment.EquipmentTestFixture.randomCreateEquipmentCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SecurityIntegrationTest extends IntegrationTest {
    @Test
    void should_throw_401_error_if_jwt_not_provided() {
        QErrorResponse response = restTestClient.post()
                .uri("/equipments")
                .body(randomCreateEquipmentCommand())
                .exchange().expectStatus().isUnauthorized()
                .expectBody(QErrorResponse.class).returnResult().getResponseBody();

        Error error = response.error();
        assertEquals(401, error.status());
        assertEquals(AUTHENTICATION_FAILED, error.code());
    }
}
