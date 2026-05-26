package com.company.andy.feature.systemsettings.domain;

import com.company.andy.common.model.AggregateRoot;
import com.company.andy.common.model.actor.SystemActor;
import com.company.andy.feature.systemsettings.domain.event.SystemBaseSettingsUpdatedEvent;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

import static com.company.andy.feature.systemsettings.domain.SystemSettings.SYSTEM_SETTINGS_COLLECTION;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Getter
@FieldNameConstants
@TypeAlias(SYSTEM_SETTINGS_COLLECTION)
@Document(SYSTEM_SETTINGS_COLLECTION)
@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator)
public class SystemSettings extends AggregateRoot {
    public final static String SYSTEM_SETTINGS_COLLECTION = "system_settings";
    public static final String SYSTEM_SETTINGS_ID = "THE_ONLY_ONE_SYSTEM_SETTINGS";

    private BaseSettings baseSettings;

    public SystemSettings(SystemActor actor) {
        super(SYSTEM_SETTINGS_ID, actor);
        baseSettings = new BaseSettings(List.of());
    }

    public void updateBaseSettings(BaseSettings newSettings, SystemActor actor) {
        if (Objects.equals(this.baseSettings, newSettings)) {
            return;
        }

        BaseSettings oldBaseSettings = this.baseSettings;
        this.baseSettings = newSettings;
        raiseEvent(new SystemBaseSettingsUpdatedEvent(oldBaseSettings, this, actor));
    }

    @Override
    protected boolean isSystemLevelObject() {
        return true;
    }
}
