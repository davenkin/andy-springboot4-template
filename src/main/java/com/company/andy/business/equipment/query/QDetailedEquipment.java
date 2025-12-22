package com.company.andy.business.equipment.query;

import com.company.andy.business.equipment.domain.EquipmentStatus;
import lombok.Builder;

import java.time.Instant;

@Builder
public record QDetailedEquipment(
        String id,
        String orgId,
        String name,
        EquipmentStatus status,
        Instant createdAt,
        String createdBy) {
}
