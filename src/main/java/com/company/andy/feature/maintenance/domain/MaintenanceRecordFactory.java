package com.company.andy.feature.maintenance.domain;

import com.company.andy.common.model.actor.Actor;
import com.company.andy.feature.equipment.domain.Equipment;
import com.company.andy.feature.equipment.domain.EquipmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Always use factories to create aggregate root objects,
// which makes the creation process of aggregate roots more explicit

@Component
@RequiredArgsConstructor
public class MaintenanceRecordFactory {

    public MaintenanceRecord create(Equipment equipment,
                                    EquipmentStatus status,
                                    String description,
                                    Actor actor) {
        return new MaintenanceRecord(equipment.getId(), equipment.getName(), status, description, actor);
    }
}
