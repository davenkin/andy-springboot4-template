package com.company.andy.common.configuration.profile;

import org.springframework.context.annotation.Profile;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

// Enable for integration tests, covering both "local-it" and "local-it-embedded" profiles
@Retention(RUNTIME)
@Profile("local-it-embedded | local-it")
public @interface EnableOnlyForIT {
}
