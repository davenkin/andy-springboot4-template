package com.company.andy.feature.equipment.command;

import static com.company.andy.TestFixture.randomOrgUserActor;
import static com.company.andy.common.event.DomainEventType.EQUIPMENT_NAME_UPDATED_EVENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.company.andy.IntegrationTest;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.feature.equipment.EquipmentTestFixture;
import com.company.andy.feature.equipment.domain.Equipment;
import com.company.andy.feature.equipment.domain.EquipmentRepository;
import com.company.andy.feature.equipment.domain.EquipmentSummary;
import com.company.andy.feature.equipment.domain.event.EquipmentNameUpdatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class EquipmentCommandServiceIntegrationTest extends IntegrationTest {
  @Autowired
  private EquipmentCommandService equipmentCommandService;

  @Autowired
  private EquipmentRepository equipmentRepository;

  @Test
  void should_update_equipment_name() {
    Actor actor = randomOrgUserActor();

    CreateEquipmentCommand createEquipmentCommand = EquipmentTestFixture.randomCreateEquipmentCommand();
    String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, actor);

    UpdateEquipmentNameCommand updateEquipmentNameCommand = EquipmentTestFixture.randomUpdateEquipmentNameCommand();
    equipmentCommandService.updateEquipmentName(equipmentId, updateEquipmentNameCommand, actor);

    Equipment equipment = equipmentRepository.byId(equipmentId);
    assertEquals(updateEquipmentNameCommand.name(), equipment.getName());
    EquipmentNameUpdatedEvent equipmentNameUpdatedEvent = latestEventFor(equipmentId, EQUIPMENT_NAME_UPDATED_EVENT,
        EquipmentNameUpdatedEvent.class);
    assertEquals(equipmentId, equipmentNameUpdatedEvent.getEquipmentId());
    assertEquals(updateEquipmentNameCommand.name(), equipmentNameUpdatedEvent.getUpdatedName());
  }

  @Test
  void should_evict_org_equipment_summaries_cache_after_new_equipment_added() throws InterruptedException {
    //Prepare data
    Actor actor = randomOrgUserActor();
    String cacheKey = "Cache:ORG_EQUIPMENTS::" + actor.orgId();
    assertFalse(stringRedisTemplate.hasKey(cacheKey));
    CreateEquipmentCommand createEquipmentCommand = new CreateEquipmentCommand(EquipmentTestFixture.randomEquipmentName());
    equipmentCommandService.createEquipment(createEquipmentCommand, actor);
    assertFalse(stringRedisTemplate.hasKey(cacheKey));
    List<EquipmentSummary> equipmentSummaries = equipmentRepository.cachedEquipmentSummaries(actor.orgId());
    assertNotNull(equipmentSummaries);
    List<EquipmentSummary> cachedEquipmentSummaries = equipmentRepository.cachedEquipmentSummaries(actor.orgId());
    assertNotNull(cachedEquipmentSummaries);
    assertTrue(stringRedisTemplate.hasKey(cacheKey));

    // Create another equipment to evict the cache
    String anotherEquipmentId = equipmentCommandService.createEquipment(
        new CreateEquipmentCommand(EquipmentTestFixture.randomEquipmentName()), actor);
    Thread.sleep(100);//wait for cache to evict

    // Verify results
    assertFalse(stringRedisTemplate.hasKey(cacheKey));
  }
}
