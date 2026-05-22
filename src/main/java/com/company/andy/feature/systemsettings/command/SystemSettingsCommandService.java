package com.company.andy.feature.systemsettings.command;

import com.company.andy.common.model.actor.SystemActor;
import com.company.andy.feature.systemsettings.domain.SystemSettings;
import com.company.andy.feature.systemsettings.domain.SystemSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SystemSettingsCommandService {
    private final SystemSettingsRepository systemSettingsRepository;

    @Transactional
    public void updateBaseSettings(UpdateSystemBaseSettingsCommand command, SystemActor actor) {
        SystemSettings systemSettings = systemSettingsRepository.getSystemSettings();
        systemSettings.updateBaseSettings(command.baseSettings(), actor);
        log.info("System base settings updated.");
    }
}
