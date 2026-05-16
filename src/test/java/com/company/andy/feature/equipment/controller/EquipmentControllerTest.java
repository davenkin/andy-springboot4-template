package com.company.andy.feature.equipment.controller;

import com.company.andy.IntegrationTest;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.util.ResponseId;
import com.company.andy.feature.equipment.command.CreateEquipmentCommand;
import com.company.andy.feature.equipment.command.UpdateEquipmentNameCommand;
import com.company.andy.feature.equipment.domain.Equipment;
import com.company.andy.feature.equipment.domain.EquipmentRepository;
import com.company.andy.feature.equipment.domain.event.EquipmentCreatedEvent;
import com.company.andy.feature.equipment.domain.event.EquipmentNameUpdatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import java.util.function.Consumer;

import static com.company.andy.TestFixture.randomOrgUserActor;
import static com.company.andy.common.event.DomainEventType.EQUIPMENT_CREATED_EVENT;
import static com.company.andy.common.event.DomainEventType.EQUIPMENT_NAME_UPDATED_EVENT;
import static com.company.andy.feature.equipment.EquipmentTestFixture.randomCreateEquipmentCommand;
import static com.company.andy.feature.equipment.EquipmentTestFixture.randomUpdateEquipmentNameCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EquipmentControllerTest extends IntegrationTest {
    @Autowired
    private EquipmentRepository equipmentRepository;

    @Test
    void should_create_equipment() {
        // Prepare
        Actor actor = randomOrgUserActor();
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();

        // Execute
        ResponseId responseId = this.restTestClient.post()
                .uri("/equipments").headers(bearerTokenHeaderFor(actor))
                .body(createEquipmentCommand)
                .exchange().expectStatus().isCreated()
                .expectBody(ResponseId.class).returnResult().getResponseBody();

        // Verify
        String equipmentId = responseId.id();
        Equipment equipment = equipmentRepository.byId(equipmentId);
        assertEquals(createEquipmentCommand.name(), equipment.getName());
        assertEquals(actor.orgId(), equipment.getOrgId());

        // Verify domain events
        // Only need to check the existence of domain event in database,
        // For event handling please add another test
        EquipmentCreatedEvent equipmentCreatedEvent = latestEventFor(equipmentId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        assertEquals(equipmentId, equipmentCreatedEvent.getEquipmentId());
    }

    @Test
    void should_update_equipment_name() {
        // Prepare
        Actor actor = randomOrgUserActor();
        Consumer<HttpHeaders> authHeader = bearerTokenHeaderFor(actor);

        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();
        String equipmentId = this.restTestClient.post()
                .uri("/equipments").headers(authHeader)
                .body(createEquipmentCommand)
                .exchange().expectStatus().isCreated()
                .expectBody(ResponseId.class).returnResult().getResponseBody().id();

        // Execute
        UpdateEquipmentNameCommand updateEquipmentNameCommand = randomUpdateEquipmentNameCommand();
        this.restTestClient.put()
                .uri("/equipments/{id}/name", equipmentId).headers(authHeader)
                .body(updateEquipmentNameCommand)
                .exchange().expectStatus().isOk();

        // Verify
        Equipment equipment = equipmentRepository.byId(equipmentId);
        assertEquals(updateEquipmentNameCommand.name(), equipment.getName());

        // Verify domain events
        EquipmentNameUpdatedEvent equipmentNameUpdatedEvent = latestEventFor(equipmentId, EQUIPMENT_NAME_UPDATED_EVENT, EquipmentNameUpdatedEvent.class);
        assertEquals(equipmentId, equipmentNameUpdatedEvent.getEquipmentId());
        assertEquals(updateEquipmentNameCommand.name(), equipmentNameUpdatedEvent.getUpdatedName());
    }
}