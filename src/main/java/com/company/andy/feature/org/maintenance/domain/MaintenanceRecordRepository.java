package com.company.andy.feature.org.maintenance.domain;

import static com.company.andy.common.util.CommonUtils.requireNonBlank;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.Optional;

import com.company.andy.common.infrastructure.AbstractMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MaintenanceRecordRepository extends AbstractMongoRepository<MaintenanceRecord> {
  public Optional<MaintenanceRecord> latestForOptional(String equipmentId) {
    requireNonBlank(equipmentId, "equipmentId must not be blank.");

    Query query = Query.query(where(MaintenanceRecord.Fields.equipmentId).is(equipmentId));
    return Optional.ofNullable(mongoTemplate.findOne(query, MaintenanceRecord.class));
  }
}
