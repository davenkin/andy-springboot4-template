package com.company.andy.feature.maintenance;

import com.company.andy.CommonRandomTestFixture;
import com.company.andy.feature.equipment.EquipmentTextFixture;
import com.company.andy.feature.maintenance.command.CreateMaintenanceRecordCommand;

public class MaintenanceRecordTestFixture {
    public static CreateMaintenanceRecordCommand randomCreateMaintenanceRecordCommand(String equipmentId) {
        return CreateMaintenanceRecordCommand.builder()
                .equipmentId(equipmentId)
                .description(CommonRandomTestFixture.randomDescription())
                .status(EquipmentTextFixture.randomEquipmentStatus())
                .build();
    }
}
