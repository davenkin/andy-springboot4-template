package com.company.andy.support;

import org.jspecify.annotations.NullMarked;
import org.springframework.test.context.ActiveProfilesResolver;

import java.util.Arrays;

@NullMarked
public class TestingActiveProfilesResolver implements ActiveProfilesResolver {
    private static final String ACTIVE_PROFILES_PROPERTY = "spring.profiles.active";
    private static final String DEFAULT_PROFILE = "local-it-embedded";

    @Override
    public String[] resolve(Class<?> testClass) {
        String activeProfiles = System.getProperty(ACTIVE_PROFILES_PROPERTY);
        if (activeProfiles == null || activeProfiles.isBlank()) {
            return new String[]{DEFAULT_PROFILE};
        }

        String[] profiles = Arrays.stream(activeProfiles.split(","))
                .map(String::trim)
                .filter(profile -> !profile.isEmpty())
                .toArray(String[]::new);
        if (profiles.length == 0) {
            throw new IllegalArgumentException("System property spring.profiles.active must contain at least one profile.");
        }
        return profiles;
    }
}
