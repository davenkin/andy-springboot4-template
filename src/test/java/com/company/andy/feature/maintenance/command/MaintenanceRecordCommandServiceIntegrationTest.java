package com.company.andy.feature.maintenance.command;

import com.company.andy.IntegrationTest;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.feature.equipment.EquipmentTextFixture;
import com.company.andy.feature.equipment.command.CreateEquipmentCommand;
import com.company.andy.feature.equipment.command.EquipmentCommandService;
import com.company.andy.feature.maintenance.MaintenanceRecordTestFixture;
import com.company.andy.feature.maintenance.domain.MaintenanceRecord;
import com.company.andy.feature.maintenance.domain.MaintenanceRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.company.andy.CommonRandomTestFixture.randomOrgUserActor;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MaintenanceRecordCommandServiceIntegrationTest extends IntegrationTest {

    @Autowired
    private MaintenanceRecordCommandService maintenanceRecordCommandService;

    @Autowired
    private EquipmentCommandService equipmentCommandService;

    @Autowired
    private MaintenanceRecordRepository maintenanceRecordRepository;

    @Test
    void should_create_maintenance_record() {
        Actor actor = randomOrgUserActor();
        CreateEquipmentCommand createEquipmentCommand = EquipmentTextFixture.randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, actor);

        CreateMaintenanceRecordCommand createMaintenanceRecordCommand = MaintenanceRecordTestFixture.randomCreateMaintenanceRecordCommand(equipmentId);
        String maintenanceRecordId = maintenanceRecordCommandService.createMaintenanceRecord(createMaintenanceRecordCommand, actor);

        MaintenanceRecord maintenanceRecord = maintenanceRecordRepository.byId(maintenanceRecordId);
        assertEquals(equipmentId, maintenanceRecord.getEquipmentId());
    }
}
