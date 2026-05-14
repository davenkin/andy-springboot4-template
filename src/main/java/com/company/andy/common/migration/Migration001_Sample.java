package com.company.andy.common.migration;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

// Mongock migration class
// Class name should follow format: "Migration[3 digits index]_[SimpleDescriptionOfYourMigration]"
@Slf4j
@ChangeUnit(id = "Migration001_Sample", order = "001", author = "andy")
public class Migration001_Sample {

    @Execution
    public void execute(MongoTemplate mongoTemplate) {
        // use mongoTemplate to manipulate the database
        log.debug("Sample change log executed.");
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        // roll back
        log.debug("Sample change log rolled back.");
    }
}
