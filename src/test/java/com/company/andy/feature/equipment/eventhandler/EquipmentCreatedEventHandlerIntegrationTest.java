package com.company.andy.feature.equipment.eventhandler;

import com.company.andy.IntegrationTest;
import com.company.andy.RandomTestUtils;
import com.company.andy.common.model.operator.Operator;
import com.company.andy.feature.equipment.command.CreateEquipmentCommand;
import com.company.andy.feature.equipment.command.EquipmentCommandService;
import com.company.andy.feature.equipment.domain.EquipmentRepository;
import com.company.andy.feature.equipment.domain.EquipmentSummary;
import com.company.andy.feature.equipment.domain.event.EquipmentCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.company.andy.common.event.DomainEventType.EQUIPMENT_CREATED_EVENT;
import static org.junit.jupiter.api.Assertions.*;

class EquipmentCreatedEventHandlerIntegrationTest extends IntegrationTest {
    @Autowired
    private EquipmentCreatedEventHandler equipmentCreatedEventHandler;

    @Autowired
    private EquipmentCommandService equipmentCommandService;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Test
    void should_evict_org_equipment_summaries_cache() throws InterruptedException {
        Operator operator = RandomTestUtils.randomUserOperator();
        CreateEquipmentCommand createEquipmentCommand = new CreateEquipmentCommand(RandomTestUtils.randomEquipmentName());
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, operator);
        String cacheKey = "Cache:ORG_EQUIPMENTS::" + operator.getOrgId();
        assertFalse(stringRedisTemplate.hasKey(cacheKey));
        List<EquipmentSummary> equipmentSummaries = equipmentRepository.cachedEquipmentSummaries(operator.getOrgId());
        assertNotNull(equipmentSummaries);
        List<EquipmentSummary> cachedEquipmentSummaries = equipmentRepository.cachedEquipmentSummaries(operator.getOrgId());
        assertNotNull(cachedEquipmentSummaries);
        assertTrue(stringRedisTemplate.hasKey(cacheKey));
        EquipmentCreatedEvent equipmentCreatedEvent = latestEventFor(equipmentId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);

        equipmentCreatedEventHandler.handle(equipmentCreatedEvent);
        Thread.sleep(100);//wait for cache to evict
        assertFalse(stringRedisTemplate.hasKey(cacheKey));
    }
}
