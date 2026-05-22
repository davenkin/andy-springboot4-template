package com.company.andy.feature.maintenance.domain.task;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.company.andy.feature.maintenance.domain.MaintenanceRecord;
import com.mongodb.client.result.DeleteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteAllMaintenanceRecordsUnderEquipmentTask {
  private final MongoTemplate mongoTemplate;

  public void run(String equipmentId) {
    Query query = query(where(MaintenanceRecord.Fields.equipmentId).is(equipmentId));
    DeleteResult result = mongoTemplate.remove(query, MaintenanceRecord.class);
    log.info("Delete all {} maintenance records under equipment [{}].", result.getDeletedCount(), equipmentId);
  }
}
