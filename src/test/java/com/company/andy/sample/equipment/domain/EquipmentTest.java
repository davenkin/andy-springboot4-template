package com.company.andy.sample.equipment.domain;

import com.company.andy.RandomTestUtils;
import com.company.andy.common.model.operator.Operator;
import org.junit.jupiter.api.Test;

import static com.company.andy.common.event.DomainEventType.EQUIPMENT_CREATED_EVENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EquipmentTest {
    @Test
    void shouldCreateEquipment() {
        Operator operator = RandomTestUtils.randomUserOperator();
        Equipment equipment = new Equipment("name", operator);
        assertEquals("name", equipment.getName());
        assertEquals(1, equipment.getEvents().size());
        assertTrue(equipment.getEvents().stream()
                .anyMatch(domainEvent -> domainEvent.getType() == EQUIPMENT_CREATED_EVENT));
    }
}
