package com.company.andy.common.init;

import com.company.andy.common.event.consume.ConsumingEvent;
import com.company.andy.common.event.publish.PublishingDomainEvent;
import com.company.andy.feature.equipment.domain.Equipment;
import com.company.andy.feature.maintenance.domain.MaintenanceRecord;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

import static com.company.andy.common.util.Constants.CHINA_TIME_ZONE;
import static java.util.Locale.CHINESE;
import static java.util.TimeZone.getTimeZone;
import static java.util.TimeZone.setDefault;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.CollectionOptions.just;
import static org.springframework.data.mongodb.core.query.Collation.of;

@Slf4j
@Component
@NullMarked
@RequiredArgsConstructor
public class StartupInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final MongoTemplate mongoTemplate;
    private final CacheClearingInitializer cacheClearingInitializer;

    @PostConstruct
    void init() {
        setDefault(getTimeZone(ZoneId.of(CHINA_TIME_ZONE)));
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        cacheClearingInitializer.clearCaches();
        ensureMongoCollectionExist();
        ensureMongoIndexExist();
        log.info("System initialized after startup.");
    }

    private void ensureMongoCollectionExist() {
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

    private void ensureMongoIndexExist() {
        ensurePublishingDomainEventIndexes();
        ensureConsumingDomainEventIndexes();
        ensureEquipmentIndexes();
        ensureMaintenanceRecordIndexes();
    }

    private void ensurePublishingDomainEventIndexes() {
        IndexOperations indexOperations = mongoTemplate.indexOps(PublishingDomainEvent.class);
        indexOperations.createIndex(new Index().on(PublishingDomainEvent.Fields.status, DESC));
        indexOperations.createIndex(new Index().on(PublishingDomainEvent.Fields.publishedCount, DESC));
        indexOperations.createIndex(new Index().on(PublishingDomainEvent.Fields.raisedAt, DESC));
    }

    private void ensureConsumingDomainEventIndexes() {
        IndexOperations indexOperations = mongoTemplate.indexOps(ConsumingEvent.class);
        indexOperations.createIndex(new Index().on(ConsumingEvent.Fields.eventId, DESC));
        indexOperations.createIndex(new Index().on(ConsumingEvent.Fields.handler, DESC));
    }

    private void ensureEquipmentIndexes() {
        IndexOperations indexOperations = mongoTemplate.indexOps(Equipment.class);
        indexOperations.createIndex(new Index().on(Equipment.Fields.name, DESC));
        indexOperations.createIndex(new Index().on(Equipment.Fields.status, DESC));
    }

    private void ensureMaintenanceRecordIndexes() {
        IndexOperations indexOperations = mongoTemplate.indexOps(MaintenanceRecord.class);
        indexOperations.createIndex(new Index().on(MaintenanceRecord.Fields.equipmentId, DESC));
        indexOperations.createIndex(new Index().on(MaintenanceRecord.Fields.equipmentName, DESC));
        indexOperations.createIndex(new Index().on(MaintenanceRecord.Fields.status, DESC));
    }
}
