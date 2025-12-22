package com.company.andy.feature.equipment.infrastructure;

import com.company.andy.feature.equipment.domain.EquipmentSummary;

import java.util.List;

// Some json deserialization configurations require a wrapper outside List
public record CachedOrgEquipmentSummaries(List<EquipmentSummary> summaries) {
}
