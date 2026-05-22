package com.company.andy.feature.maintenance;

import static com.company.andy.TestFixture.randomDescription;
import static com.company.andy.feature.equipment.EquipmentTestFixture.randomEquipmentStatus;

import com.company.andy.feature.maintenance.command.CreateMaintenanceRecordCommand;

public class MaintenanceRecordTestFixture {
  public static CreateMaintenanceRecordCommand randomCreateMaintenanceRecordCommand(String equipmentId) {
    return CreateMaintenanceRecordCommand.builder()
        .equipmentId(equipmentId)
        .description(randomDescription())
        .status(randomEquipmentStatus())
        .build();
  }
}
