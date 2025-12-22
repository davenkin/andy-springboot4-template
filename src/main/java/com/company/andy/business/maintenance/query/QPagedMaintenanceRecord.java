package com.company.andy.business.maintenance.query;

import com.company.andy.business.equipment.domain.EquipmentStatus;
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
