package com.company.andy.business.maintenance.domain;

import com.company.andy.business.equipment.domain.Equipment;
import com.company.andy.business.equipment.domain.EquipmentStatus;
import com.company.andy.common.model.operator.Operator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MaintenanceRecordFactory {

    public MaintenanceRecord create(Equipment equipment,
                                    EquipmentStatus status,
                                    String description,
                                    Operator operator) {
        return new MaintenanceRecord(equipment.getId(), equipment.getName(), status, description, operator);
    }
}
