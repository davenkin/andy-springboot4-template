package com.company.andy.support;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import redis.embedded.RedisServer;

import java.io.IOException;

@Slf4j
@Component
@Profile("local-it-embedded")
public class TestingEmbeddedRedisServer {
    @Value("${spring.data.redis.port}")
    private int redisPort;

    private static RedisServer redisServer;

    @PostConstruct
    public synchronized void startRedisServer() {
        if (redisServer != null) {
            return;
        }

        try {
            redisServer = new RedisServer(redisPort);
            redisServer.start();
            log.info("Embedded Redis Server started.");
        } catch (IOException e) {
            log.warn("Failed to start embedded Redis server.", e);
        }
    }

    @PreDestroy
    public synchronized void stopRedisServer() {
        if (redisServer == null) {
            return;
        }

        try {
            redisServer.stop();
            log.info("Embedded Redis Server stopped.");
        } catch (IOException e) {
            log.warn("Failed to stop embedded Redis server", e);
        }
    }
}
