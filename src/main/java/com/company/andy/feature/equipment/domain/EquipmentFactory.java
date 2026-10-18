package com.company.andy.feature.equipment.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Always use factories to create AggregateRoot, as it makes the creation process of AggregateRoot more explicit.

@Component
@RequiredArgsConstructor
public class EquipmentFactory {

    public Equipment create(String name, String orgId) {
        return new Equipment(name, orgId);
    }

    public Equipment create(String equipmentId, String name, EquipmentEngine engine, String orgId) {
        return new Equipment(equipmentId, name, orgId, engine);
    }
}
