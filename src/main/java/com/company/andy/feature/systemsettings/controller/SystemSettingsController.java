package com.company.andy.feature.systemsettings.controller;

import com.company.andy.common.model.actor.SystemActor;
import com.company.andy.feature.systemsettings.command.SystemSettingsCommandService;
import com.company.andy.feature.systemsettings.command.UpdateSystemBaseSettingsCommand;
import com.company.andy.feature.systemsettings.query.QSystemSettings;
import com.company.andy.feature.systemsettings.query.SystemSettingsQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

// This controller is not for org level actors,
// it's for system admins to manage the system level resources

@Profile("local | it | it-local")
@Tag(name = "SystemSettingsController", description = "System settings APIs")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/system/system-settings")
public class SystemSettingsController {
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
            @RequestBody @Valid UpdateSystemBaseSettingsCommand command,
            @AuthenticationPrincipal SystemActor actor) {
        systemSettingsCommandService.updateBaseSettings(command, actor);
    }

}
