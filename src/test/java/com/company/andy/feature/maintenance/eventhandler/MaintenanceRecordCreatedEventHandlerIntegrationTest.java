package com.company.andy.feature.maintenance.eventhandler;

import com.company.andy.IntegrationTest;
import com.company.andy.common.model.operator.Operator;
import com.company.andy.feature.equipment.command.CreateEquipmentCommand;
import com.company.andy.feature.equipment.command.EquipmentCommandService;
import com.company.andy.feature.equipment.domain.Equipment;
import com.company.andy.feature.equipment.domain.EquipmentRepository;
import com.company.andy.feature.maintenance.command.CreateMaintenanceRecordCommand;
import com.company.andy.feature.maintenance.command.MaintenanceRecordCommandService;
import com.company.andy.feature.maintenance.domain.event.MaintenanceRecordCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.company.andy.RandomTestUtils.*;
import static com.company.andy.common.event.DomainEventType.MAINTENANCE_RECORD_CREATED_EVENT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MaintenanceRecordCreatedEventHandlerIntegrationTest extends IntegrationTest {
    @Autowired
    private EquipmentCommandService equipmentCommandService;

    @Autowired
    private MaintenanceRecordCommandService maintenanceRecordCommandService;

    @Autowired
    private MaintenanceRecordCreatedEventHandler maintenanceRecordCreatedEventHandler;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Test
    void should_count_maintenance_records_for_equipment() {
        Operator operator = randomUserOperator();
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, operator);
        CreateMaintenanceRecordCommand createMaintenanceRecordCommand = randomCreateMaintenanceRecordCommand(equipmentId);
        String maintenanceRecordId = maintenanceRecordCommandService.createMaintenanceRecord(createMaintenanceRecordCommand, operator);
        String maintenanceRecordId2 = maintenanceRecordCommandService.createMaintenanceRecord(createMaintenanceRecordCommand, operator);
        MaintenanceRecordCreatedEvent createdEvent = latestEventFor(maintenanceRecordId, MAINTENANCE_RECORD_CREATED_EVENT, MaintenanceRecordCreatedEvent.class);
        MaintenanceRecordCreatedEvent createdEvent2 = latestEventFor(maintenanceRecordId2, MAINTENANCE_RECORD_CREATED_EVENT, MaintenanceRecordCreatedEvent.class);

        maintenanceRecordCreatedEventHandler.handle(createdEvent);
        maintenanceRecordCreatedEventHandler.handle(createdEvent2);

        assertEquals(2, equipmentRepository.byId(equipmentId).getMaintenanceRecordCount());
    }

    @Test
    void should_update_status_for_equipment_using_maintenance_record_status() {
        Operator operator = randomUserOperator();
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, operator);
        CreateMaintenanceRecordCommand createMaintenanceRecordCommand = randomCreateMaintenanceRecordCommand(equipmentId);
        String maintenanceRecordId = maintenanceRecordCommandService.createMaintenanceRecord(createMaintenanceRecordCommand, operator);

        MaintenanceRecordCreatedEvent createdEvent = latestEventFor(maintenanceRecordId, MAINTENANCE_RECORD_CREATED_EVENT, MaintenanceRecordCreatedEvent.class);
        maintenanceRecordCreatedEventHandler.handle(createdEvent);

        Equipment equipment = equipmentRepository.byId(equipmentId);
        assertEquals(createMaintenanceRecordCommand.status(), equipment.getStatus());
    }

}
