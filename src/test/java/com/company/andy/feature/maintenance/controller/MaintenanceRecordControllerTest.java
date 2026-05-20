package com.company.andy.feature.maintenance.controller;

import com.company.andy.IntegrationTest;
import com.company.andy.common.model.actor.OrgActor;
import com.company.andy.common.util.PagedResponse;
import com.company.andy.common.util.ResponseId;
import com.company.andy.feature.org.equipment.command.CreateEquipmentCommand;
import com.company.andy.feature.org.equipment.command.EquipmentCommandService;
import com.company.andy.feature.org.maintenance.command.CreateMaintenanceRecordCommand;
import com.company.andy.feature.org.maintenance.command.MaintenanceRecordCommandService;
import com.company.andy.feature.org.maintenance.domain.MaintenanceRecord;
import com.company.andy.feature.org.maintenance.domain.MaintenanceRecordRepository;
import com.company.andy.feature.org.maintenance.query.PageMaintenanceRecordsQuery;
import com.company.andy.feature.org.maintenance.query.QPagedMaintenanceRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;

import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.company.andy.TestFixture.randomHumanUserOrgActor;
import static com.company.andy.feature.equipment.EquipmentTestFixture.randomCreateEquipmentCommand;
import static com.company.andy.feature.maintenance.MaintenanceRecordTestFixture.randomCreateMaintenanceRecordCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MaintenanceRecordControllerTest extends IntegrationTest {
    @Autowired
    private MaintenanceRecordRepository maintenanceRecordRepository;

    @Autowired
    private EquipmentCommandService equipmentCommandService;

    @Autowired
    private MaintenanceRecordCommandService maintenanceRecordCommandService;

    @Test
    void should_create_maintenance_record() {
        // Prepare
        OrgActor actor = randomHumanUserOrgActor();
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, actor);

        // Execute
        CreateMaintenanceRecordCommand createMaintenanceRecordCommand = randomCreateMaintenanceRecordCommand(equipmentId);
        String maintenanceRecordId = restTestClient.post()
                .uri("/maintenance-records").headers(authHeaderOf(actor))
                .body(createMaintenanceRecordCommand)
                .exchange().expectStatus().isCreated()
                .expectBody(ResponseId.class).returnResult().getResponseBody().id();

        // Verify
        MaintenanceRecord maintenanceRecord = maintenanceRecordRepository.byId(maintenanceRecordId);
        assertEquals(equipmentId, maintenanceRecord.getEquipmentId());
    }


    @Test
    void should_page_maintenance_records() {
        // Prepare
        OrgActor actor = randomHumanUserOrgActor();
        Consumer<HttpHeaders> authHeader = authHeaderOf(actor);
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, actor);
        IntStream.range(0, 20).forEach(_ -> maintenanceRecordCommandService.createMaintenanceRecord(randomCreateMaintenanceRecordCommand(equipmentId), actor));

        // Execute
        PageMaintenanceRecordsQuery query = PageMaintenanceRecordsQuery.builder().pageSize(12).build();
        PagedResponse<QPagedMaintenanceRecord> records = restTestClient.post()
                .uri("/maintenance-records/paged").headers(authHeader)
                .body(query)
                .exchange().expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PagedResponse<QPagedMaintenanceRecord>>() {
                }).returnResult().getResponseBody();

        // Verify
        assertEquals(12, records.content().size());
    }
}