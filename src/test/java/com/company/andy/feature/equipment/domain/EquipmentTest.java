package com.company.andy.feature.equipment.domain;

import com.company.andy.CommonRandomTestFixture;
import com.company.andy.common.model.operator.Operator;
import org.junit.jupiter.api.Test;

import static com.company.andy.common.event.DomainEventType.EQUIPMENT_CREATED_EVENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EquipmentTest {
    @Test
    void shouldCreateEquipment() {
        Operator operator = CommonRandomTestFixture.randomOrgUserOperator();
        Equipment equipment = new Equipment("name", operator);
        assertEquals("name", equipment.getName());
        assertEquals(1, equipment.getEvents().size());
        assertTrue(equipment.getEvents().stream()
                .anyMatch(domainEvent -> domainEvent.getType() == EQUIPMENT_CREATED_EVENT));
    }
}
