package com.company.andy.feature.equipment.domain;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository {
    void save(Equipment equipment);

    void save(List<Equipment> equipments);

    void delete(Equipment equipment);

    void delete(List<Equipment> equipments);

    Equipment byId(String id);

    Equipment byId(String id, String orgId);

    Optional<Equipment> byIdOptional(String id);

    Optional<Equipment> byIdOptional(String id, String orgId);

    boolean exists(String id, String orgId);

    List<EquipmentSummary> cachedEquipmentSummaries(String orgId);

    void evictCachedEquipmentSummaries(String orgId);

    boolean existsByName(String name, String orgId);
}

