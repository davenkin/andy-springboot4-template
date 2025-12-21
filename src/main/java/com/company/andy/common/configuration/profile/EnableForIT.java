package com.company.andy.common.configuration.profile;

import org.springframework.context.annotation.Profile;

import java.lang.annotation.Retention;

import static com.company.andy.common.util.Constants.IT_PROFILE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

// Enable for integration tests
@Retention(RUNTIME)
@Profile(IT_PROFILE)
public @interface EnableForIT {
}
