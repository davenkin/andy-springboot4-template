package com.company.andy.feature.systemsettings.domain;

import com.company.andy.common.infrastructure.AbstractMongoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import static com.company.andy.common.model.actor.SystemActor.createJobSystemActor;
import static com.company.andy.common.utils.Constants.SYSTEM_SETTINGS_CACHE;
import static com.company.andy.feature.systemsettings.domain.SystemSettings.SYSTEM_SETTINGS_ID;

@Repository
@RequiredArgsConstructor
public class SystemSettingsRepository extends AbstractMongoRepository<SystemSettings> {
    private final SystemSettingsFactory systemSettingsFactory;

    public SystemSettings getSystemSettings() {
        return super.byId(SYSTEM_SETTINGS_ID);
    }

    @Cacheable(value = SYSTEM_SETTINGS_CACHE)
    public SystemSettings cachedSystemSettings() {
        return super.byIdOptional(SYSTEM_SETTINGS_ID).orElse(null);
    }

    @Override
    @CacheEvict(value = SYSTEM_SETTINGS_CACHE, allEntries = true)
    public void save(SystemSettings systemSettings) {
        super.save(systemSettings);
    }

    @PostConstruct
    public void init() {
        if (!exists(SYSTEM_SETTINGS_ID)) {
            SystemSettings initSystemSettings = systemSettingsFactory.createSystemSettings(createJobSystemActor("InitSystemSettings"));
            super.save(initSystemSettings);
        }
    }
}
