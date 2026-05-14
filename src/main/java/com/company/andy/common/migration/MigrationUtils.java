package com.company.andy.common.migration;

import org.springframework.data.mongodb.core.MongoTemplate;

import static java.util.Locale.CHINESE;
import static org.springframework.data.mongodb.core.CollectionOptions.just;
import static org.springframework.data.mongodb.core.query.Collation.of;

public class MigrationUtils {

    public static void createCollection(Class<?> collectionClass, MongoTemplate mongoTemplate) {
        if (!mongoTemplate.collectionExists(collectionClass)) {
            mongoTemplate.createCollection(collectionClass, just(of(CHINESE).numericOrderingEnabled()));
        }
    }
}
