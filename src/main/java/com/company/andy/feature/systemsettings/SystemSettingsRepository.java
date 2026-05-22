package com.company.andy.feature.systemsettings;

import com.company.andy.common.infrastructure.AbstractMongoRepository;

import java.util.Optional;

public class SystemSettingsRepository extends AbstractMongoRepository<SystemSettings> {
    public Optional<SystemSettings> findTheSettings() {
        return Optional.empty();
    }
}
