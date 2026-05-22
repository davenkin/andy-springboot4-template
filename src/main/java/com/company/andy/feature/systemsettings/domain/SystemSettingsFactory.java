package com.company.andy.feature.systemsettings.domain;

import com.company.andy.common.model.actor.SystemActor;
import org.springframework.stereotype.Component;

@Component
public class SystemSettingsFactory {
    public SystemSettings createSystemSettings(SystemActor actor) {
        return new SystemSettings(actor);
    }
}
