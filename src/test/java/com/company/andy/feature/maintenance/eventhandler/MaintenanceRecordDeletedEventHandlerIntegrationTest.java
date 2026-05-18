package com.company.andy.feature.maintenance.eventhandler;

import com.company.andy.IntegrationTest;
import com.company.andy.common.model.actor.OrgActor;
import com.company.andy.feature.equipment.command.CreateEquipmentCommand;
import com.company.andy.feature.equipment.command.EquipmentCommandService;
import com.company.andy.feature.equipment.domain.Equipment;
import com.company.andy.feature.equipment.domain.EquipmentRepository;
import com.company.andy.feature.maintenance.command.CreateMaintenanceRecordCommand;
import com.company.andy.feature.maintenance.command.MaintenanceRecordCommandService;
import com.company.andy.feature.maintenance.domain.event.MaintenanceRecordDeletedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import static com.company.andy.TestFixture.randomHumanUserOrgActor;
import static com.company.andy.common.event.DomainEventType.MAINTENANCE_RECORD_DELETED_EVENT;
import static com.company.andy.feature.equipment.EquipmentTestFixture.randomCreateEquipmentCommand;
import static com.company.andy.feature.maintenance.MaintenanceRecordTestFixture.randomCreateMaintenanceRecordCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MaintenanceRecordDeletedEventHandlerIntegrationTest extends IntegrationTest {
    @Autowired
    private EquipmentCommandService equipmentCommandService;

    @Autowired
    private MaintenanceRecordCommandService maintenanceRecordCommandService;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private MaintenanceRecordDeletedEventHandler maintenanceRecordDeletedEventHandler;

    @Test
    void delete_maintenance_record_should_re_count_records_for_equipment() {
        // Prepare
        OrgActor actor = randomHumanUserOrgActor();
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, actor);
        Equipment equipment = equipmentRepository.byId(equipmentId);
        ReflectionTestUtils.setField(equipment, "maintenanceRecordCount", 2);
        equipmentRepository.save(equipment);
        assertEquals(2, equipmentRepository.byId(equipmentId).getMaintenanceRecordCount());
        CreateMaintenanceRecordCommand createMaintenanceRecordCommand = randomCreateMaintenanceRecordCommand(equipmentId);
        String maintenanceRecordId = maintenanceRecordCommandService.createMaintenanceRecord(createMaintenanceRecordCommand, actor);
        maintenanceRecordCommandService.deleteMaintenanceRecord(maintenanceRecordId, actor);
        MaintenanceRecordDeletedEvent maintenanceRecordDeletedEvent = latestEventFor(maintenanceRecordId, MAINTENANCE_RECORD_DELETED_EVENT, MaintenanceRecordDeletedEvent.class);

        // Execute
        maintenanceRecordDeletedEventHandler.handle(maintenanceRecordDeletedEvent);

        // Verify
        assertEquals(0, equipmentRepository.byId(equipmentId).getMaintenanceRecordCount());
    }
}