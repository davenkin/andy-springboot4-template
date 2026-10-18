package com.company.andy.support;

import com.company.andy.common.configuration.profile.EnableOnlyForIT;
import com.company.andy.common.ratelimiter.RateLimiter;
import org.springframework.stereotype.Component;

@Component
@EnableOnlyForIT
public class TestingRateLimiter implements RateLimiter {
    @Override
    public void applyFor(String orgId, String key, int tps) {

    }

    @Override
    public void applyFor(String key, int tps) {

    }
}
