package com.company.andy.feature.org.maintenance;

import com.company.andy.feature.org.maintenance.command.CreateMaintenanceRecordCommand;

import static com.company.andy.TestFixture.randomDescription;
import static com.company.andy.feature.org.equipment.EquipmentTestFixture.randomEquipmentStatus;

public class MaintenanceRecordTestFixture {
    public static CreateMaintenanceRecordCommand randomCreateMaintenanceRecordCommand(String equipmentId) {
        return CreateMaintenanceRecordCommand.builder()
                .equipmentId(equipmentId)
                .description(randomDescription())
                .status(randomEquipmentStatus())
                .build();
    }
}
