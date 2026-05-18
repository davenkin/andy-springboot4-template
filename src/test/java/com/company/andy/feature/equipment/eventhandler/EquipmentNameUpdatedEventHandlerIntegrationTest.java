package com.company.andy.feature.equipment.eventhandler;

import com.company.andy.IntegrationTest;
import com.company.andy.TestFixture;
import com.company.andy.common.model.actor.OrgActor;
import com.company.andy.feature.equipment.command.CreateEquipmentCommand;
import com.company.andy.feature.equipment.command.EquipmentCommandService;
import com.company.andy.feature.equipment.command.UpdateEquipmentNameCommand;
import com.company.andy.feature.equipment.domain.event.EquipmentNameUpdatedEvent;
import com.company.andy.feature.maintenance.command.CreateMaintenanceRecordCommand;
import com.company.andy.feature.maintenance.command.MaintenanceRecordCommandService;
import com.company.andy.feature.maintenance.domain.MaintenanceRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.company.andy.common.event.DomainEventType.EQUIPMENT_NAME_UPDATED_EVENT;
import static com.company.andy.feature.equipment.EquipmentTestFixture.randomCreateEquipmentCommand;
import static com.company.andy.feature.maintenance.MaintenanceRecordTestFixture.randomCreateMaintenanceRecordCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EquipmentNameUpdatedEventHandlerIntegrationTest extends IntegrationTest {
    @Autowired
    private EquipmentCommandService equipmentCommandService;

    @Autowired
    private MaintenanceRecordCommandService maintenanceRecordCommandService;

    @Autowired
    private EquipmentNameUpdatedEventHandler equipmentNameUpdatedEventHandler;

    @Autowired
    private MaintenanceRecordRepository maintenanceRecordRepository;

    @Test
    void update_equipment_name_should_update_maintenance_records_equipment_name() {
        // Prepare
        OrgActor actor = TestFixture.randomHumanUserOrgActor();
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, actor);

        CreateMaintenanceRecordCommand createMaintenanceRecordCommand = randomCreateMaintenanceRecordCommand(equipmentId);
        String maintenanceRecordId = maintenanceRecordCommandService.createMaintenanceRecord(createMaintenanceRecordCommand, actor);
        assertEquals(createEquipmentCommand.name(), maintenanceRecordRepository.byId(maintenanceRecordId).getEquipmentName());

        UpdateEquipmentNameCommand updateEquipmentNameCommand = UpdateEquipmentNameCommand.builder().name("updatedName").build();
        equipmentCommandService.updateEquipmentName(equipmentId, updateEquipmentNameCommand, actor);
        EquipmentNameUpdatedEvent equipmentNameUpdatedEvent = latestEventFor(equipmentId, EQUIPMENT_NAME_UPDATED_EVENT, EquipmentNameUpdatedEvent.class);

        // Execute
        equipmentNameUpdatedEventHandler.handle(equipmentNameUpdatedEvent);

        // Verify
        assertEquals(updateEquipmentNameCommand.name(), maintenanceRecordRepository.byId(maintenanceRecordId).getEquipmentName());
    }

}