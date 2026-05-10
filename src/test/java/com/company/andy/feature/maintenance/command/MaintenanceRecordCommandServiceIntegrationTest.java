package com.company.andy.feature.maintenance.command;

import com.company.andy.IntegrationTest;
import com.company.andy.common.model.operator.Operator;
import com.company.andy.feature.equipment.EquipmentTextFixture;
import com.company.andy.feature.equipment.command.CreateEquipmentCommand;
import com.company.andy.feature.equipment.command.EquipmentCommandService;
import com.company.andy.feature.maintenance.MaintenanceRecordTestFixture;
import com.company.andy.feature.maintenance.domain.MaintenanceRecord;
import com.company.andy.feature.maintenance.domain.MaintenanceRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.company.andy.CommonRandomTestFixture.randomOrgUserOperator;
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
        Operator operator = randomOrgUserOperator();
        CreateEquipmentCommand createEquipmentCommand = EquipmentTextFixture.randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, operator);

        CreateMaintenanceRecordCommand createMaintenanceRecordCommand = MaintenanceRecordTestFixture.randomCreateMaintenanceRecordCommand(equipmentId);
        String maintenanceRecordId = maintenanceRecordCommandService.createMaintenanceRecord(createMaintenanceRecordCommand, operator);

        MaintenanceRecord maintenanceRecord = maintenanceRecordRepository.byId(maintenanceRecordId);
        assertEquals(equipmentId, maintenanceRecord.getEquipmentId());
    }
}
