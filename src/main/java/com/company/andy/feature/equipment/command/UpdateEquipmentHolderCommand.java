package com.company.andy.feature.equipment.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateEquipmentHolderCommand(
        @Schema(description = "Holder name of the equipment")
        @NotBlank @Size(max = 100) String name) {
}
