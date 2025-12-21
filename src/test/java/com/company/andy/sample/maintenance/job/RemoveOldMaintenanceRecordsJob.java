package com.company.andy.sample.maintenance.job;

import com.company.andy.common.model.AggregateRoot;
import com.company.andy.sample.maintenance.domain.MaintenanceRecord;
import com.mongodb.client.result.DeleteResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.springframework.data.mongodb.core.query.Criteria.where;

// Remove MaintenanceRecords that are more than KEEP_DAYS days old
@Slf4j
@Component
@AllArgsConstructor
public class RemoveOldMaintenanceRecordsJob {
    private static final int KEEP_DAYS = 180;
    private final MongoTemplate mongoTemplate;

    public void run() {
        log.info("Start removing maintenance records that are more than {} days old.", KEEP_DAYS);
        Query query = Query.query(where(AggregateRoot.Fields.createdAt).lt(Instant.now().minus(KEEP_DAYS, DAYS)));
        DeleteResult result = mongoTemplate.remove(query, MaintenanceRecord.class);
        log.info("Removed {} maintenance records that are more than {} days old.", KEEP_DAYS, result.getDeletedCount());
    }
}
