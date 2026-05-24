package com.company.andy.feature.maintenance.domain;

import com.company.andy.common.model.actor.OrgActor;
import com.company.andy.common.model.actor.SystemActor;
import com.company.andy.feature.equipment.domain.Equipment;
import com.company.andy.feature.equipment.domain.EquipmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Always use factories to create Aggregate Root objects,
// which makes the creation process of Aggregate Roots more explicit

@Component
@RequiredArgsConstructor
public class MaintenanceRecordFactory {

    public MaintenanceRecord create(
            Equipment equipment,
            EquipmentStatus status,
            String description,
            OrgActor actor) {
        return new MaintenanceRecord(equipment, status, description, actor);
    }

    public MaintenanceRecord createFromExternal(
            Equipment equipment,
            EquipmentStatus status,
            String description,
            String externalRecordId,
            SystemActor systemActor) {
        return new MaintenanceRecord(equipment, status, description, externalRecordId, systemActor);
    }
}
