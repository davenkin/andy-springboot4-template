package com.company.andy.feature.maintenance.controller;

import com.company.andy.IntegrationTest;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.util.PagedResponse;
import com.company.andy.common.util.ResponseId;
import com.company.andy.feature.equipment.command.CreateEquipmentCommand;
import com.company.andy.feature.maintenance.command.CreateMaintenanceRecordCommand;
import com.company.andy.feature.maintenance.domain.MaintenanceRecord;
import com.company.andy.feature.maintenance.domain.MaintenanceRecordRepository;
import com.company.andy.feature.maintenance.query.PageMaintenanceRecordsQuery;
import com.company.andy.feature.maintenance.query.QPagedMaintenanceRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;

import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.company.andy.TestFixture.randomOrgUserActor;
import static com.company.andy.feature.equipment.EquipmentTestFixture.randomCreateEquipmentCommand;
import static com.company.andy.feature.maintenance.MaintenanceRecordTestFixture.randomCreateMaintenanceRecordCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MaintenanceRecordControllerTest extends IntegrationTest {
    @Autowired
    private MaintenanceRecordRepository maintenanceRecordRepository;

    @Test
    void should_create_maintenance_record() {
        // Prepare
        Actor actor = randomOrgUserActor();
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();
        String equipmentId = restTestClient.post()
                .uri("/equipments").headers(authHeaderOf(actor))
                .body(createEquipmentCommand)
                .exchange().expectStatus().isCreated()
                .expectBody(ResponseId.class).returnResult().getResponseBody().id();

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
        Actor actor = randomOrgUserActor();
        Consumer<HttpHeaders> authHeader = authHeaderOf(actor);
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();
        String equipmentId = restTestClient.post()
                .uri("/equipments").headers(authHeader)
                .body(createEquipmentCommand)
                .exchange().expectStatus().isCreated()
                .expectBody(ResponseId.class).returnResult().getResponseBody().id();
        IntStream.range(0, 20).forEach(i -> {
            restTestClient.post()
                    .uri("/maintenance-records").headers(authHeader)
                    .body(randomCreateMaintenanceRecordCommand(equipmentId))
                    .exchange().expectStatus().isCreated()
                    .expectBody(ResponseId.class).returnResult().getResponseBody().id();
        });

        // Execute
        PageMaintenanceRecordsQuery query = PageMaintenanceRecordsQuery.builder().pageSize(12).build();
        PagedResponse<QPagedMaintenanceRecord> records = restTestClient.post()
                .uri("/maintenance-records/paged").headers(authHeader)
                .body(query)
                .exchange().expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PagedResponse<QPagedMaintenanceRecord>>() {
                }).returnResult().getResponseBody();

        // Verify
        assertEquals(12, records.getContent().size());
    }
}