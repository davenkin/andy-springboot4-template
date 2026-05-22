package com.company.andy.common.configuration;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationFeature;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;
import static com.fasterxml.jackson.annotation.PropertyAccessor.ALL;
import static tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static tools.jackson.databind.MapperFeature.REQUIRE_SETTERS_FOR_GETTERS;
import static tools.jackson.databind.cfg.DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS;
import static tools.jackson.databind.cfg.DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS;

@EnableResilientMethods
@Configuration(proxyBeanMethods = false)
public class CommonConfiguration {

    @Bean
    public JsonMapperBuilderCustomizer jsonMapperBuilderCustomizer() {
        return builder -> builder
                .changeDefaultVisibility(it -> it.withVisibility(ALL, ANY))
                .changeDefaultPropertyInclusion(it -> it.withValueInclusion(ALWAYS))
                .enable(REQUIRE_SETTERS_FOR_GETTERS)
                .disable(FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(WRITE_DATES_AS_TIMESTAMPS)
                .disable(WRITE_DURATIONS_AS_TIMESTAMPS);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }
}
