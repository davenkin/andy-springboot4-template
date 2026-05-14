package com.company.andy.common.configuration.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("common")
public record CommonProperties(boolean limitRate) {}
