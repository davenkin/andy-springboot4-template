package com.company.andy.feature.systemsettings.domain;

import org.springframework.stereotype.Component;

import java.util.List;

// Always use factories to create AggregateRoot, as it makes the creation process of AggregateRoot more explicit.

@Component
public class SystemSettingsFactory {
    public SystemSettings createSystemSettings() {
        return new SystemSettings(new BaseSettings(List.of()));
    }
}
