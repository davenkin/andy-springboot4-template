package com.company.andy.feature.systemsettings;

import java.util.Optional;

import com.company.andy.common.infrastructure.AbstractMongoRepository;

public class SystemSettingsRepository extends AbstractMongoRepository<SystemSettings> {
  public Optional<SystemSettings> findTheSettings() {
    return Optional.empty();
  }
}
