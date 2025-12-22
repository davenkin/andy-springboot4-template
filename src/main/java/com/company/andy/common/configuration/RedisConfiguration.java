package com.company.andy.common.configuration;

import com.company.andy.business.equipment.infrastructure.CachedOrgEquipmentSummaries;
import com.company.andy.common.util.Constants;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import tools.jackson.databind.ObjectMapper;

import static com.company.andy.common.util.Constants.ORG_EQUIPMENTS_CACHE;
import static java.time.Duration.ofDays;
import static org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig;
import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

@EnableCaching
@Configuration(proxyBeanMethods = false)
public class RedisConfiguration {

    @Bean
    public RedisCacheManagerBuilderCustomizer redisBuilderCustomizer(ObjectMapper objectMapper) {
        return builder -> builder
                .cacheDefaults(defaultCacheConfig()
                        .prefixCacheNameWith(Constants.CACHE_PREFIX)
                        .serializeValuesWith(fromSerializer(new GenericJacksonJsonRedisSerializer(objectMapper)))
                        .entryTtl(ofDays(7)))
                .withCacheConfiguration(ORG_EQUIPMENTS_CACHE, defaultCacheConfig()
                        .prefixCacheNameWith(Constants.CACHE_PREFIX)
                        .serializeValuesWith(fromSerializer(new JacksonJsonRedisSerializer<>(objectMapper, CachedOrgEquipmentSummaries.class)))
                        .entryTtl(ofDays(7)))
                ;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }
}
