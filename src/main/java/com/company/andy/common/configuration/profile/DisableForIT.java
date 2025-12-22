package com.company.andy.common.configuration.profile;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import org.springframework.context.annotation.Profile;

// Disable for integration tests
@Retention(RUNTIME)
@Profile("!(it | it-local)")
public @interface DisableForIT {
}
