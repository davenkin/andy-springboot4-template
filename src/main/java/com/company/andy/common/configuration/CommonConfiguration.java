package com.company.andy.common.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.resilience.annotation.EnableResilientMethods;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;

@EnableAsync
@EnableResilientMethods
@Configuration(proxyBeanMethods = false)
public class CommonConfiguration {

  @Bean
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(50);
    executor.setQueueCapacity(500);
    executor.initialize();
    executor.setThreadNamePrefix("default-");
    return executor;
  }

  @Bean
  public JsonMapperBuilderCustomizer jsonMapperBuilderCustomizer() {
    return builder -> builder
        .changeDefaultVisibility(checker ->
            checker.withVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
        )
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(DateTimeFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
  }
}
