package com.company.andy.common.configuration.property;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("common")
public record CommonProperties(@NotNull String orgJwtIssuer,
                               @NotNull String platformJwtIssuer,
                               @NotNull String deviceJwtIssuer) {
}
