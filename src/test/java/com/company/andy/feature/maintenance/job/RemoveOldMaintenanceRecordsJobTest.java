package com.company.andy.feature.maintenance.job;

import static java.time.temporal.ChronoUnit.DAYS;

import static com.company.andy.TestFixture.randomHumanUserOrgActor;
import static com.company.andy.common.model.OrgRole.ORG_ADMIN;
import static com.company.andy.common.utils.Constants.MONGO_ID;
import static com.company.andy.feature.equipment.EquipmentTestFixture.randomCreateEquipmentCommand;
import static com.company.andy.feature.maintenance.MaintenanceRecordTestFixture.randomCreateMaintenanceRecordCommand;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.time.Instant;

import com.company.andy.IntegrationTest;
import com.company.andy.common.model.AggregateRoot;
import com.company.andy.common.model.actor.OrgActor;
import com.company.andy.feature.equipment.command.EquipmentCommandService;
import com.company.andy.feature.maintenance.command.CreateMaintenanceRecordCommand;
import com.company.andy.feature.maintenance.command.MaintenanceRecordCommandService;
import com.company.andy.feature.maintenance.domain.MaintenanceRecord;
import com.company.andy.feature.maintenance.domain.MaintenanceRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

class RemoveOldMaintenanceRecordsJobTest extends IntegrationTest {
  @Autowired
  private MaintenanceRecordCommandService maintenanceRecordCommandService;

  @Autowired
  private EquipmentCommandService equipmentCommandService;

  @Autowired
  private RemoveOldMaintenanceRecordsJob removeOldMaintenanceRecordsJob;

  @Autowired
  private MaintenanceRecordRepository maintenanceRecordRepository;

  @Test
  void should_remove_old_maintenance_records() {
    // Prepare
    OrgActor actor = randomHumanUserOrgActor(ORG_ADMIN);
    String equipmentId = equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), actor);
    CreateMaintenanceRecordCommand createMaintenanceRecordCommand = randomCreateMaintenanceRecordCommand(equipmentId);
    String maintenanceRecordId = maintenanceRecordCommandService.createMaintenanceRecord(createMaintenanceRecordCommand, actor);
    String oldMaintenanceRecordId = maintenanceRecordCommandService.createMaintenanceRecord(createMaintenanceRecordCommand, actor);

    Query query = Query.query(where(MONGO_ID).is(oldMaintenanceRecordId));
    Update update = new Update().set(AggregateRoot.Fields.createdAt, Instant.now().minus(500, DAYS));
    mongoTemplate.updateFirst(query, update, MaintenanceRecord.class);

    // Execute
    removeOldMaintenanceRecordsJob.run();

    // Verify
    assertFalse(maintenanceRecordRepository.exists(oldMaintenanceRecordId));
    assertTrue(maintenanceRecordRepository.exists(maintenanceRecordId));
  }
}
