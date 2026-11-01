package com.company.andy.common.configuration.profile;

import org.springframework.context.annotation.Profile;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

// Disable for all local profiles, covering both "local", "local-it" and "local-it-embedded"
@Retention(RUNTIME)
@Profile("!(local | local-it | local-it-embedded)")
public @interface DisableForLocal {
}
