package com.company.andy.feature.maintenance.query;

import java.time.Instant;

import com.company.andy.feature.equipment.domain.EquipmentStatus;
import lombok.Builder;

@Builder
public record QPagedMaintenanceRecord(
    String id,
    String equipmentId,
    String equipmentName,
    EquipmentStatus status,
    String orgId,
    Instant createdAt,
    String createdBy) {
}
