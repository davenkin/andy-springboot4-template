package com.company.andy.sample.maintenance.query;

import com.company.andy.sample.equipment.domain.EquipmentStatus;
import lombok.Builder;

import java.time.Instant;

@Builder
public record QDetailedMaintenanceRecord(
        String id,
        String equipmentId,
        String equipmentName,
        EquipmentStatus status,
        String description,
        String orgId,
        Instant createdAt,
        String createdBy) {
}
