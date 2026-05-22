package com.company.andy.feature.maintenance.query;

import java.time.Instant;

import com.company.andy.feature.equipment.domain.EquipmentStatus;
import lombok.Builder;

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
