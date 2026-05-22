package com.company.andy.feature.systemsettings.controller;

import com.company.andy.IntegrationTest;
import com.company.andy.common.model.actor.OrgActor;
import com.company.andy.common.model.actor.SystemActor;
import com.company.andy.feature.systemsettings.command.UpdateSystemBaseSettingsCommand;
import com.company.andy.feature.systemsettings.domain.BaseSettings;
import com.company.andy.feature.systemsettings.domain.SystemSettingsRepository;
import com.company.andy.feature.systemsettings.query.QSystemSettings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.company.andy.TestFixture.*;
import static com.company.andy.common.model.OrgRole.ORG_ADMIN;
import static com.company.andy.common.utils.Constants.SYSTEM_SETTINGS_CACHE;
import static com.company.andy.feature.systemsettings.domain.SystemSettings.SYSTEM_SETTINGS_ID;
import static org.junit.jupiter.api.Assertions.*;

class SystemSettingsControllerTest extends IntegrationTest {

    @Autowired
    private SystemSettingsRepository systemSettingsRepository;

    @Test
    void should_get_system_settings() {
        SystemActor actor = randomHumanUserSystemActor();

        QSystemSettings systemSettings = restTestClient.get()
                .uri("/system/system-settings").headers(authHeaderOf(actor))
                .exchange().expectStatus().isOk()
                .expectBody(QSystemSettings.class).returnResult().getResponseBody();
        assertNotNull(systemSettings);
    }

    @Test
    void should_update_system_base_settings() {
        // Prepare
        SystemActor actor = randomHumanUserSystemActor();
        UpdateSystemBaseSettingsCommand updateCommand = UpdateSystemBaseSettingsCommand.builder()
                .baseSettings(BaseSettings.builder().demoReservationNotificationEmails(List.of(randomEmail())).build())
                .build();

        // Execute
        restTestClient.put()
                .uri("/system/system-settings/base-settings").headers(authHeaderOf(actor))
                .body(updateCommand)
                .exchange().expectStatus().isOk();

        // Verify
        assertEquals(updateCommand.baseSettings().demoReservationNotificationEmails(),
                systemSettingsRepository.getSystemSettings().getBaseSettings().demoReservationNotificationEmails());
    }

    @Test
    void save_system_settings_should_evict_cache() {
        // Prepare
        SystemActor actor = randomHumanUserSystemActor();
        QSystemSettings systemSettings = restTestClient.get()
                .uri("/system/system-settings").headers(authHeaderOf(actor))
                .exchange().expectStatus().isOk()
                .expectBody(QSystemSettings.class).returnResult().getResponseBody();
        assertNotNull(systemSettings);
        assertNotNull(cacheManager.getCache(SYSTEM_SETTINGS_CACHE).get(SYSTEM_SETTINGS_ID));

        // Execute
        UpdateSystemBaseSettingsCommand updateCommand = UpdateSystemBaseSettingsCommand.builder()
                .baseSettings(BaseSettings.builder().demoReservationNotificationEmails(List.of(randomEmail())).build())
                .build();
        restTestClient.put()
                .uri("/system/system-settings/base-settings").headers(authHeaderOf(actor))
                .body(updateCommand)
                .exchange().expectStatus().isOk();

        // Verify
        assertNull(cacheManager.getCache(SYSTEM_SETTINGS_CACHE).get(SYSTEM_SETTINGS_ID));
        QSystemSettings updatedCachedSettings = restTestClient.get()
                .uri("/system/system-settings").headers(authHeaderOf(actor))
                .exchange().expectStatus().isOk()
                .expectBody(QSystemSettings.class).returnResult().getResponseBody();
        assertNotNull(updatedCachedSettings);
        assertNotNull(cacheManager.getCache(SYSTEM_SETTINGS_CACHE).get(SYSTEM_SETTINGS_ID));
    }

    @Test
    void org_users_are_not_allowed_to_access_system_settings() {
        OrgActor actor = randomHumanUserOrgActor(ORG_ADMIN);
        restTestClient.get()
                .uri("/system/system-settings").headers(authHeaderOf(actor))
                .exchange().expectStatus().isForbidden();
    }
}