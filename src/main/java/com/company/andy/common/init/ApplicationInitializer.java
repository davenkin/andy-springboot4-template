package com.company.andy.common.init;

import static java.util.Locale.CHINESE;

import static org.springframework.data.mongodb.core.CollectionOptions.just;
import static org.springframework.data.mongodb.core.query.Collation.of;

import com.company.andy.common.event.consume.ConsumingEvent;
import com.company.andy.common.event.publish.PublishingDomainEvent;
import com.company.andy.feature.org.equipment.domain.Equipment;
import com.company.andy.feature.org.maintenance.domain.MaintenanceRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NullMarked
@RequiredArgsConstructor
public class ApplicationInitializer implements SmartLifecycle {
  private final MongoTemplate mongoTemplate;
  private final CacheManager cacheManager;
  private volatile boolean running = false;

  private void ensureMongoCollectionsExists() {
    createCollection(PublishingDomainEvent.class);
    createCollection(ConsumingEvent.class);
    createCollection(Equipment.class);
    createCollection(MaintenanceRecord.class);
    log.info("Created all MongoDB collections.");
  }

  private void clearCaches() {
    this.cacheManager.getCacheNames().forEach(cacheName -> {
      Cache cache = cacheManager.getCache(cacheName);
      if (cache != null) {
        cache.clear();
      }
    });
    log.info("Cleared application caches.");
  }

  private void createCollection(Class<?> collectionClass) {
    if (!mongoTemplate.collectionExists(collectionClass)) {
      mongoTemplate.createCollection(collectionClass, just(of(CHINESE).numericOrderingEnabled()));
    }
  }

  @Override
  public void start() {
    ensureMongoCollectionsExists();
    clearCaches();
    running = true;
  }

  @Override
  public void stop() {running = false;}

  @Override
  public boolean isRunning() {return running;}

  @Override
  public int getPhase() {
    return 0;
  }
}
