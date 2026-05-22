package com.company.andy.feature.systemsettings.query;

import com.company.andy.feature.systemsettings.domain.BaseSettings;
import lombok.Builder;

@Builder
public record QSystemSettings(
        BaseSettings baseSettings) {
}
