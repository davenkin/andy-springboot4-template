package com.company.andy.feature.org.equipment.eventhandler.external;

import static com.company.andy.TestFixture.randomExternalEventId;
import static com.company.andy.TestFixture.randomOrgId;
import static com.company.andy.feature.org.equipment.EquipmentTestFixture.randomEquipmentName;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import com.company.andy.IntegrationTest;
import com.company.andy.feature.org.equipment.domain.Equipment;
import com.company.andy.feature.org.equipment.domain.EquipmentEngine;
import com.company.andy.feature.org.equipment.domain.EquipmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ExternalEquipmentInstalledEventHandlerTest extends IntegrationTest {

  @Autowired
  private EquipmentRepository equipmentRepository;

  @Test
  void should_create_equipment_for_external_equipment_installed_event() {
    // Prepare
    ExternalEquipmentInstalledEvent event = ExternalEquipmentInstalledEvent.builder()
        .eventId(randomExternalEventId())
        .equipmentId(UUID.randomUUID().toString())
        .orgId(randomOrgId())
        .name(randomEquipmentName())
        .engine(new EquipmentEngine("Super model"))
        .build();

    // Execute
    eventConsumer.consumeExternalEvent(event);

    // Verify
    Equipment equipment = equipmentRepository.byId(event.getEquipmentId());
    assertEquals(event.getName(), equipment.getName());
  }
}
