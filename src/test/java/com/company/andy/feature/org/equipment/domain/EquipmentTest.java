package com.company.andy.feature.org.equipment.domain;

import static com.company.andy.TestFixture.randomHumanUserOrgActor;
import static com.company.andy.common.event.DomainEventType.EQUIPMENT_CREATED_EVENT;
import static com.company.andy.common.model.OrgRole.ORG_ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.company.andy.common.model.actor.OrgActor;
import org.junit.jupiter.api.Test;

class EquipmentTest {
  @Test
  void should_create_equipment() {
    OrgActor actor = randomHumanUserOrgActor(ORG_ADMIN);
    Equipment equipment = new Equipment("name", actor);
    assertEquals("name", equipment.getName());
    assertEquals(1, equipment.getEvents().size());
    assertTrue(equipment.getEvents().stream()
        .anyMatch(domainEvent -> domainEvent.getType() == EQUIPMENT_CREATED_EVENT));
  }
}
