package com.company.andy.feature.equipment.controller;

import static com.company.andy.TestFixture.randomOrgUserActor;
import static com.company.andy.common.event.DomainEventType.EQUIPMENT_CREATED_EVENT;
import static com.company.andy.feature.equipment.EquipmentTestFixture.randomCreateEquipmentCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.company.andy.IntegrationTest;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.util.ResponseId;
import com.company.andy.feature.equipment.command.CreateEquipmentCommand;
import com.company.andy.feature.equipment.domain.Equipment;
import com.company.andy.feature.equipment.domain.EquipmentRepository;
import com.company.andy.feature.equipment.domain.event.EquipmentCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class EquipmentControllerTest extends IntegrationTest {
  @Autowired
  private EquipmentRepository equipmentRepository;

  @Test
  void should_create_equipment() {
    //Prepare
    Actor actor = randomOrgUserActor();
    CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();

    //Execute
    ResponseId responseId = this.restTestClient.post()
        .uri("/equipments").headers(bearerTokenHeaderFor(actor))
        .body(createEquipmentCommand)
        .exchange().expectStatus().isCreated()
        .expectBody(ResponseId.class).returnResult().getResponseBody();

    //Verify results
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
}