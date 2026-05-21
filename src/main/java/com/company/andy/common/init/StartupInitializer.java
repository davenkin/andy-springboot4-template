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
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@NullMarked
@RequiredArgsConstructor
public class StartupInitializer implements ApplicationListener<ApplicationReadyEvent> {
  private final CacheClearingInitializer cacheClearingInitializer;
  private final MongoTemplate mongoTemplate;

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    ensureMongoCollectionsExists();
    cacheClearingInitializer.clearCaches();
    log.info("System initialized after startup.");
  }

  private void ensureMongoCollectionsExists() {
    createCollection(PublishingDomainEvent.class);
    createCollection(ConsumingEvent.class);
    createCollection(Equipment.class);
    createCollection(MaintenanceRecord.class);
  }

  private void createCollection(Class<?> collectionClass) {
    if (!mongoTemplate.collectionExists(collectionClass)) {
      mongoTemplate.createCollection(collectionClass, just(of(CHINESE).numericOrderingEnabled()));
    }
  }
}
