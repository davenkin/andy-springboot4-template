package com.company.andy.feature.org.maintenance.command;

import com.company.andy.feature.org.equipment.domain.EquipmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

// Command objects are the mutation data you send to the controller,
// which usually results in state change in the system

@Builder
public record CreateMaintenanceRecordCommand(
        @NotBlank String equipmentId,
        @NotBlank @Size(max = 1000) String description,
        @NotNull EquipmentStatus status
) {
}
