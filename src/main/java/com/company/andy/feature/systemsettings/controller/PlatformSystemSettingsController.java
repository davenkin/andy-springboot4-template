package com.company.andy.feature.systemsettings.controller;

import com.company.andy.feature.systemsettings.command.SystemSettingsCommandService;
import com.company.andy.feature.systemsettings.command.UpdateSystemBaseSettingsCommand;
import com.company.andy.feature.systemsettings.query.QSystemSettings;
import com.company.andy.feature.systemsettings.query.SystemSettingsQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

// Controller should be thin and calls into CommandService and QueryService.
// Here PlatformSystemSettingsController belongs to the Platform API plane and should be prefixed with "Platform".

@Profile("local | it-embedded | it-local")
@Tag(name = "PlatformSystemSettingsController", description = "System settings APIs for Platform API plane")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/platform/system-settings")
public class PlatformSystemSettingsController {
    private final SystemSettingsCommandService systemSettingsCommandService;
    private final SystemSettingsQueryService systemSettingsQueryService;

    @Operation(summary = "Get system settings")
    @GetMapping
    public QSystemSettings getSystemSettings() {
        return systemSettingsQueryService.getSystemSettings();
    }

    @Operation(summary = "Update system base settings")
    @PutMapping("/base-settings")
    public void updateBaseSetting(
            @RequestBody @Valid UpdateSystemBaseSettingsCommand command) {
        systemSettingsCommandService.updateBaseSettings(command);
    }
}
