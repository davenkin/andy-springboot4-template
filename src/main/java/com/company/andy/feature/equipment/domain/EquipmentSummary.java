package com.company.andy.feature.equipment.domain;

import lombok.Builder;

// Summary info object contains the key information of the object.

@Builder
public record EquipmentSummary(String id,
                               String orgId,
                               String name,
                               EquipmentStatus status) {
}
