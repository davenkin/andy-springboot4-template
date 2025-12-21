package com.company.andy.sample.equipment.eventhandler;

import com.company.andy.IntegrationTest;
import com.company.andy.RandomTestUtils;
import com.company.andy.common.model.operator.Operator;
import com.company.andy.sample.equipment.command.CreateEquipmentCommand;
import com.company.andy.sample.equipment.command.EquipmentCommandService;
import com.company.andy.sample.equipment.domain.EquipmentRepository;
import com.company.andy.sample.equipment.domain.EquipmentSummary;
import com.company.andy.sample.equipment.domain.event.EquipmentCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.company.andy.common.event.DomainEventType.EQUIPMENT_CREATED_EVENT;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EquipmentCreatedEventHandlerIntegrationTest extends IntegrationTest {
    @Autowired
    private EquipmentCreatedEventHandler equipmentCreatedEventHandler;

    @Autowired
    private EquipmentCommandService equipmentCommandService;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Test
    void should_evict_org_equipment_summaries_cache() {
        Operator operator = RandomTestUtils.randomUserOperator();
        CreateEquipmentCommand createEquipmentCommand = new CreateEquipmentCommand(RandomTestUtils.randomEquipmentName());
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, operator);
        String cacheKey = "Cache:ORG_EQUIPMENTS::" + operator.getOrgId();
        assertFalse(stringRedisTemplate.hasKey(cacheKey));
        List<EquipmentSummary> equipmentSummaries = equipmentRepository.cachedEquipmentSummaries(operator.getOrgId());
        assertTrue(stringRedisTemplate.hasKey(cacheKey));
        EquipmentCreatedEvent equipmentCreatedEvent = latestEventFor(equipmentId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);

        equipmentCreatedEventHandler.handle(equipmentCreatedEvent);
        assertFalse(stringRedisTemplate.hasKey(cacheKey));
    }
}
