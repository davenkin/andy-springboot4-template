package com.company.andy.common.ratelimiter;

import com.company.andy.IntegrationTest;
import com.company.andy.common.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;
import java.util.stream.IntStream;

import static com.company.andy.TestFixture.randomOrgId;
import static com.company.andy.common.exception.ErrorCode.TOO_MANY_REQUEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RedisRateLimiterIntegrationTest extends IntegrationTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void should_limit_rate_for_global_key() {
        RedisRateLimiter redisRateLimiter = new RedisRateLimiter(stringRedisTemplate);
        String key = UUID.randomUUID().toString();
        redisRateLimiter.applyFor(key, 5);
        ServiceException serviceException = assertThrows(ServiceException.class, () -> IntStream.range(0, 10).forEach(value -> redisRateLimiter.applyFor(key, 5)));
        assertEquals(TOO_MANY_REQUEST, serviceException.getCode());
    }

    @Test
    public void should_limit_rate_for_org_key() {
        RedisRateLimiter redisRateLimiter = new RedisRateLimiter(stringRedisTemplate);
        String key = UUID.randomUUID().toString();
        String orgId = randomOrgId();
        redisRateLimiter.applyFor(orgId, key, 5);
        ServiceException serviceException = assertThrows(ServiceException.class, () -> IntStream.range(0, 10).forEach(value -> redisRateLimiter.applyFor(orgId, key, 5)));
        assertEquals(TOO_MANY_REQUEST, serviceException.getCode());
    }

}