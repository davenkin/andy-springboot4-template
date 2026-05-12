package com.company.andy.feature.equipment.eventhandler;

import com.company.andy.IntegrationTest;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.feature.equipment.EquipmentTextFixture;
import com.company.andy.feature.equipment.command.CreateEquipmentCommand;
import com.company.andy.feature.equipment.command.EquipmentCommandService;
import com.company.andy.feature.equipment.domain.event.EquipmentDeletedEvent;
import com.company.andy.feature.maintenance.MaintenanceRecordTestFixture;
import com.company.andy.feature.maintenance.command.CreateMaintenanceRecordCommand;
import com.company.andy.feature.maintenance.command.MaintenanceRecordCommandService;
import com.company.andy.feature.maintenance.domain.MaintenanceRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.company.andy.CommonRandomTestFixture.randomOrgUserActor;
import static com.company.andy.common.event.DomainEventType.EQUIPMENT_DELETED_EVENT;
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
        // Prepare data
        Actor actor = randomOrgUserActor();
        CreateEquipmentCommand createEquipmentCommand = EquipmentTextFixture.randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, actor);
        CreateMaintenanceRecordCommand createMaintenanceRecordCommand = MaintenanceRecordTestFixture.randomCreateMaintenanceRecordCommand(equipmentId);
        String maintenanceRecordId = maintenanceRecordCommandService.createMaintenanceRecord(createMaintenanceRecordCommand, actor);
        assertTrue(maintenanceRecordRepository.exists(maintenanceRecordId));

        // Run event handler
        equipmentCommandService.deleteEquipment(equipmentId, actor);
        EquipmentDeletedEvent equipmentDeletedEvent = latestEventFor(equipmentId, EQUIPMENT_DELETED_EVENT, EquipmentDeletedEvent.class);
        equipmentDeletedEventEventHandler.handle(equipmentDeletedEvent);

        // Verify results
        assertFalse(maintenanceRecordRepository.exists(maintenanceRecordId));
    }
}
