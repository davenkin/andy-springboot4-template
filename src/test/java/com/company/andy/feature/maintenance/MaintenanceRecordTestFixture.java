package com.company.andy.feature.maintenance;

import com.company.andy.feature.maintenance.command.CreateMaintenanceRecordCommand;

import static com.company.andy.CommonRandomTestFixture.randomDescription;
import static com.company.andy.feature.equipment.EquipmentTextFixture.randomEquipmentStatus;

public class MaintenanceRecordTestFixture {
    public static CreateMaintenanceRecordCommand randomCreateMaintenanceRecordCommand(String equipmentId) {
        return CreateMaintenanceRecordCommand.builder()
                .equipmentId(equipmentId)
                .description(randomDescription())
                .status(randomEquipmentStatus())
                .build();
    }
}
