package com.company.andy.feature.systemsettings.query;


// Query services are used for querying data, which represent the "Q" of CQRS,
// query services can call repositories or directly use MongoTemplate to query database

import com.company.andy.feature.systemsettings.domain.SystemSettings;
import com.company.andy.feature.systemsettings.domain.SystemSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SystemSettingsQueryService {
    private final SystemSettingsRepository systemSettingsRepository;

    public QSystemSettings getSystemSettings() {
        SystemSettings systemSettings = systemSettingsRepository.getSystemSettings();
        return QSystemSettings.builder()
                .baseSettings(systemSettings.getBaseSettings())
                .build();
    }
}
