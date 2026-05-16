package com.company.andy.feature.equipment.command;

import com.company.andy.IntegrationTest;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.feature.equipment.domain.EquipmentRepository;
import com.company.andy.feature.equipment.domain.EquipmentSummary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.company.andy.TestFixture.randomOrgUserActor;
import static com.company.andy.common.util.Constants.ORG_EQUIPMENTS_CACHE;
import static com.company.andy.feature.equipment.EquipmentTestFixture.randomEquipmentName;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class EquipmentCommandServiceIntegrationTest extends IntegrationTest {
    @Autowired
    private EquipmentCommandService equipmentCommandService;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Test
    void should_evict_org_equipment_summaries_cache_after_new_equipment_added() throws InterruptedException {
        //Prepare
        Actor actor = randomOrgUserActor();
        assertNull(cacheManager.getCache(ORG_EQUIPMENTS_CACHE).get(actor.orgId()));
        CreateEquipmentCommand createEquipmentCommand = new CreateEquipmentCommand(randomEquipmentName());
        equipmentCommandService.createEquipment(createEquipmentCommand, actor);
        assertNull(cacheManager.getCache(ORG_EQUIPMENTS_CACHE).get(actor.orgId()));
        List<EquipmentSummary> equipmentSummaries = equipmentRepository.cachedEquipmentSummaries(actor.orgId()).summaries();
        assertNotNull(equipmentSummaries);
        List<EquipmentSummary> cachedEquipmentSummaries = equipmentRepository.cachedEquipmentSummaries(actor.orgId()).summaries();
        assertNotNull(cachedEquipmentSummaries);
        assertNotNull(cacheManager.getCache(ORG_EQUIPMENTS_CACHE).get(actor.orgId()));

        // Execute
        // Create another equipment to evict the cache
        equipmentCommandService.createEquipment(new CreateEquipmentCommand(randomEquipmentName()), actor);

        // Verify
        assertNull(cacheManager.getCache(ORG_EQUIPMENTS_CACHE).get(actor.orgId()));
    }
}
