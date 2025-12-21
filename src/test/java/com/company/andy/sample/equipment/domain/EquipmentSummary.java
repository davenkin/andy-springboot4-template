package com.company.andy.sample.equipment.domain;

import lombok.Builder;

@Builder
public record EquipmentSummary(String id,
                               String orgId,
                               String name,
                               EquipmentStatus status) {
}
