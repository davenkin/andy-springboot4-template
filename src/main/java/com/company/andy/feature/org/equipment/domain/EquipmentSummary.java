package com.company.andy.feature.org.equipment.domain;

import lombok.Builder;

@Builder
public record EquipmentSummary(String id,
                               String orgId,
                               String name,
                               EquipmentStatus status) {
}
