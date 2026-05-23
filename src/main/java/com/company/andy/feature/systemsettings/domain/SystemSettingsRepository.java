package com.company.andy.feature.systemsettings.domain;

import com.company.andy.common.mongo.AbstractMongoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Cacheable(value = SYSTEM_SETTINGS_CACHE, key = "'THE_ONLY_ONE_SYSTEM_SETTINGS'")
    public SystemSettings cachedSystemSettings() {
        return super.byIdOptional(SYSTEM_SETTINGS_ID).orElse(null);
    }

    @Override
    @CacheEvict(value = SYSTEM_SETTINGS_CACHE, allEntries = true)
    public void save(SystemSettings systemSettings) {
        super.save(systemSettings);
    }

    // Pre create the only SystemSettings object
    @PostConstruct
    public void init() {
        if (!exists(SYSTEM_SETTINGS_ID)) {
            SystemSettings initSystemSettings = systemSettingsFactory.createSystemSettings(createJobSystemActor("InitSystemSettings"));
            super.save(initSystemSettings);
        }
    }

    @Override
    public void save(List<SystemSettings> systemSettings) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void delete(SystemSettings systemSettings) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void delete(List<SystemSettings> systemSettings) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
