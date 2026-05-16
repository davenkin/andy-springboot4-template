package com.company.andy.feature.maintenance.query;

import com.company.andy.feature.equipment.domain.EquipmentStatus;

import java.time.Instant;

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
