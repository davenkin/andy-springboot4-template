package com.company.andy.business.equipment.query;

import com.company.andy.business.equipment.domain.EquipmentStatus;

import java.time.Instant;

public record QPagedEquipment(
        String id,
        String orgId,
        String name,
        EquipmentStatus status,
        Instant createdAt,
        String createdBy) {
}
