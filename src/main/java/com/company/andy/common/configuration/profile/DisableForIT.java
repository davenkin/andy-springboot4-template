package com.company.andy.common.configuration.profile;

import org.springframework.context.annotation.Profile;

import java.lang.annotation.Retention;

import static com.company.andy.common.util.Constants.NON_IT_PROFILE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

// Disable for integration tests
@Retention(RUNTIME)
@Profile(NON_IT_PROFILE)
public @interface DisableForIT {
}
