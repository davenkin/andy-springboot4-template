package com.company.andy.feature.systemsettings.controller;

import com.company.andy.feature.systemsettings.query.QSystemSettings;
import com.company.andy.feature.systemsettings.query.SystemSettingsQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Controller should be thin and calls into CommandService and QueryService.
// Controller should pass through the Actor to CommandService and QueryService.

@Profile("local | it-embedded | it-local")
@Tag(name = "SystemSettingsController", description = "System settings APIs")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/system-settings")
public class SystemSettingsController {
    private final SystemSettingsQueryService systemSettingsQueryService;

    @Operation(summary = "Get system settings")
    @GetMapping
    public QSystemSettings getSystemSettings() {
        return systemSettingsQueryService.getSystemSettings();
    }
}
