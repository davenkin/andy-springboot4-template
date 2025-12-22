package com.company.andy.common.configuration.profile;

import org.springframework.context.annotation.Profile;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

// Disable for integration tests
@Retention(RUNTIME)
@Profile("!(it | it-local)")
public @interface DisableForIT {
}
