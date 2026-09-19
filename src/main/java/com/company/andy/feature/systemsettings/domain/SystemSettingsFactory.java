package com.company.andy.feature.systemsettings.domain;

import com.company.andy.common.model.actor.PlatformActor;
import org.springframework.stereotype.Component;

@Component
public class SystemSettingsFactory {
    public SystemSettings createSystemSettings(PlatformActor actor) {
        return new SystemSettings(actor);
    }
}
