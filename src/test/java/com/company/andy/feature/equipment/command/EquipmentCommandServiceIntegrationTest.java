package com.company.andy.feature.equipment.command;

import com.company.andy.CommonRandomTestFixture;
import com.company.andy.IntegrationTest;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.feature.equipment.EquipmentTextFixture;
import com.company.andy.feature.equipment.domain.Equipment;
import com.company.andy.feature.equipment.domain.EquipmentRepository;
import com.company.andy.feature.equipment.domain.EquipmentSummary;
import com.company.andy.feature.equipment.domain.event.EquipmentCreatedEvent;
import com.company.andy.feature.equipment.domain.event.EquipmentNameUpdatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.company.andy.CommonRandomTestFixture.randomOrgUserActor;
import static com.company.andy.common.event.DomainEventType.EQUIPMENT_CREATED_EVENT;
import static com.company.andy.common.event.DomainEventType.EQUIPMENT_NAME_UPDATED_EVENT;
import static org.junit.jupiter.api.Assertions.*;

class EquipmentCommandServiceIntegrationTest extends IntegrationTest {
    @Autowired
    private EquipmentCommandService equipmentCommandService;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Test
    void should_create_equipment() {
        //Prepare data
        Actor actor = CommonRandomTestFixture.randomOrgUserActor();
        CreateEquipmentCommand createEquipmentCommand = EquipmentTextFixture.randomCreateEquipmentCommand();

        //Execute
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, actor);

        //Verify results
        Equipment equipment = equipmentRepository.byId(equipmentId);
        assertEquals(createEquipmentCommand.name(), equipment.getName());
        assertEquals(actor.orgId(), equipment.getOrgId());

        // Verify domain events
        // Only need to check the existence of domain event in database,
        // no need to further test event handler as that will be handled in event handlers' own tests
        EquipmentCreatedEvent equipmentCreatedEvent = latestEventFor(equipmentId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        assertEquals(equipmentId, equipmentCreatedEvent.getEquipmentId());
    }

    @Test
    void should_update_equipment_name() {
        Actor actor = CommonRandomTestFixture.randomOrgUserActor();

        CreateEquipmentCommand createEquipmentCommand = EquipmentTextFixture.randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, actor);

        UpdateEquipmentNameCommand updateEquipmentNameCommand = EquipmentTextFixture.randomUpdateEquipmentNameCommand();
        equipmentCommandService.updateEquipmentName(equipmentId, updateEquipmentNameCommand, actor);

        Equipment equipment = equipmentRepository.byId(equipmentId);
        assertEquals(updateEquipmentNameCommand.name(), equipment.getName());
        EquipmentNameUpdatedEvent equipmentNameUpdatedEvent = latestEventFor(equipmentId, EQUIPMENT_NAME_UPDATED_EVENT, EquipmentNameUpdatedEvent.class);
        assertEquals(equipmentId, equipmentNameUpdatedEvent.getEquipmentId());
        assertEquals(updateEquipmentNameCommand.name(), equipmentNameUpdatedEvent.getUpdatedName());
    }

    @Test
    void should_evict_org_equipment_summaries_cache_after_new_equipment_added() throws InterruptedException {
        //Prepare data
        Actor actor = CommonRandomTestFixture.randomOrgUserActor();
        String cacheKey = "Cache:ORG_EQUIPMENTS::" + actor.orgId();
        assertFalse(stringRedisTemplate.hasKey(cacheKey));
        CreateEquipmentCommand createEquipmentCommand = new CreateEquipmentCommand(EquipmentTextFixture.randomEquipmentName());
        equipmentCommandService.createEquipment(createEquipmentCommand, actor);
        assertFalse(stringRedisTemplate.hasKey(cacheKey));
        List<EquipmentSummary> equipmentSummaries = equipmentRepository.cachedEquipmentSummaries(actor.orgId());
        assertNotNull(equipmentSummaries);
        List<EquipmentSummary> cachedEquipmentSummaries = equipmentRepository.cachedEquipmentSummaries(actor.orgId());
        assertNotNull(cachedEquipmentSummaries);
        assertTrue(stringRedisTemplate.hasKey(cacheKey));

        // Create another equipment to evict the cache
        String anotherEquipmentId = equipmentCommandService.createEquipment(new CreateEquipmentCommand(EquipmentTextFixture.randomEquipmentName()), actor);
        Thread.sleep(100);//wait for cache to evict

        // Verify results
        assertFalse(stringRedisTemplate.hasKey(cacheKey));
    }
}
