package com.company.andy.feature.maintenance.query;

import com.company.andy.feature.equipment.domain.EquipmentStatus;
import lombok.Builder;

import java.time.Instant;

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
