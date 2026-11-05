package com.company.andy.feature.systemsettings.controller;

import com.company.andy.IntegrationTest;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.feature.systemsettings.query.QSystemSettings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;

import static com.company.andy.TestFixture.randomMemberActor;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD;

@Execution(SAME_THREAD)
class SystemSettingsControllerTest extends IntegrationTest {

    @Test
    void should_get_system_settings() {
        // Prepare
        Actor actor = randomMemberActor();

        // Execute
        QSystemSettings qSystemSettings = restTestClient.get()
                .uri("/system-settings").headers(authHeaderOf(actor))
                .exchange().expectStatus().isOk()
                .expectBody(QSystemSettings.class).returnResult().getResponseBody();

        // Verify
        assertNotNull(qSystemSettings);
    }

}