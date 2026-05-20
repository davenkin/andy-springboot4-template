package com.company.andy.feature.equipment.eventhandler;

import com.company.andy.IntegrationTest;
import com.company.andy.TestFixture;
import com.company.andy.common.model.actor.OrgActor;
import com.company.andy.feature.org.equipment.command.CreateEquipmentCommand;
import com.company.andy.feature.org.equipment.command.EquipmentCommandService;
import com.company.andy.feature.org.equipment.domain.event.EquipmentDeletedEvent;
import com.company.andy.feature.org.equipment.eventhandler.EquipmentDeletedEventEventHandler;
import com.company.andy.feature.org.maintenance.command.CreateMaintenanceRecordCommand;
import com.company.andy.feature.org.maintenance.command.MaintenanceRecordCommandService;
import com.company.andy.feature.org.maintenance.domain.MaintenanceRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.company.andy.common.event.DomainEventType.EQUIPMENT_DELETED_EVENT;
import static com.company.andy.feature.equipment.EquipmentTestFixture.randomCreateEquipmentCommand;
import static com.company.andy.feature.maintenance.MaintenanceRecordTestFixture.randomCreateMaintenanceRecordCommand;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EquipmentDeletedEventEventHandlerIntegrationTest extends IntegrationTest {
    @Autowired
    private EquipmentDeletedEventEventHandler equipmentDeletedEventEventHandler;

    @Autowired
    private EquipmentCommandService equipmentCommandService;

    @Autowired
    private MaintenanceRecordCommandService maintenanceRecordCommandService;

    @Autowired
    private MaintenanceRecordRepository maintenanceRecordRepository;

    @Test
    void delete_equipment_should_also_delete_all_its_maintenance_records() {
        // Prepare
        OrgActor actor = TestFixture.randomHumanUserOrgActor();
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, actor);
        CreateMaintenanceRecordCommand createMaintenanceRecordCommand = randomCreateMaintenanceRecordCommand(equipmentId);
        String maintenanceRecordId = maintenanceRecordCommandService.createMaintenanceRecord(createMaintenanceRecordCommand, actor);
        assertTrue(maintenanceRecordRepository.exists(maintenanceRecordId));

        // Execute
        equipmentCommandService.deleteEquipment(equipmentId, actor);
        EquipmentDeletedEvent equipmentDeletedEvent = latestEventFor(equipmentId, EQUIPMENT_DELETED_EVENT, EquipmentDeletedEvent.class);
        equipmentDeletedEventEventHandler.handle(equipmentDeletedEvent);

        // Verify
        assertFalse(maintenanceRecordRepository.exists(maintenanceRecordId));
    }
}
