package com.company.andy.feature.maintenance.query;

import com.company.andy.IntegrationTest;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.util.PagedResponse;
import com.company.andy.feature.equipment.EquipmentTestFixture;
import com.company.andy.feature.equipment.command.CreateEquipmentCommand;
import com.company.andy.feature.equipment.command.EquipmentCommandService;
import com.company.andy.feature.maintenance.MaintenanceRecordTestFixture;
import com.company.andy.feature.maintenance.command.MaintenanceRecordCommandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.IntStream;

import static com.company.andy.TestFixture.randomOrgUserActor;
import static com.company.andy.feature.equipment.EquipmentTestFixture.randomCreateEquipmentCommand;
import static com.company.andy.feature.maintenance.MaintenanceRecordTestFixture.randomCreateMaintenanceRecordCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MaintenanceRecordQueryServiceIntegrationTest extends IntegrationTest {
    @Autowired
    private EquipmentCommandService equipmentCommandService;

    @Autowired
    private MaintenanceRecordQueryService maintenanceRecordQueryService;

    @Autowired
    private MaintenanceRecordCommandService maintenanceRecordCommandService;

    @Test
    void should_page_maintenance_records() {
        Actor actor = randomOrgUserActor();
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, actor);
        IntStream.range(0, 20).forEach(i -> {
            maintenanceRecordCommandService.createMaintenanceRecord(randomCreateMaintenanceRecordCommand(equipmentId), actor);
        });

        PageMaintenanceRecordsQuery query = PageMaintenanceRecordsQuery.builder().pageSize(12).build();
        PagedResponse<QPagedMaintenanceRecord> records = maintenanceRecordQueryService.pageMaintenanceRecords(query, actor);

        assertEquals(12, records.getContent().size());
    }
}
