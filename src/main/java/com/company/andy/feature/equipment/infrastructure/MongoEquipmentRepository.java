package com.company.andy.feature.equipment.infrastructure;

import com.company.andy.common.infrastructure.AbstractMongoRepository;
import com.company.andy.common.model.AggregateRoot;
import com.company.andy.feature.equipment.domain.Equipment;
import com.company.andy.feature.equipment.domain.EquipmentRepository;
import com.company.andy.feature.equipment.domain.EquipmentSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.company.andy.common.util.CommonUtils.requireNonBlank;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Repository
@RequiredArgsConstructor
public class MongoEquipmentRepository extends AbstractMongoRepository<Equipment> implements EquipmentRepository {
    private final CachedMongoEquipmentRepository cachedMongoEquipmentRepository;

    @Override
    public List<EquipmentSummary> cachedEquipmentSummaries(String orgId) {
        return cachedMongoEquipmentRepository.cachedEquipmentSummaries(orgId).summaries();
    }

    @Override
    public void evictCachedEquipmentSummaries(String orgId) {
        cachedMongoEquipmentRepository.evictCachedEquipmentSummaries(orgId);
    }

    @Override
    public boolean existsByName(String name, String orgId) {
        requireNonBlank(name, "name must not be blank.");
        requireNonBlank(orgId, "orgId must not be blank.");
        Query query = query(where(Equipment.Fields.name).is(name).and(AggregateRoot.Fields.orgId).is(orgId));
        return mongoTemplate.exists(query, Equipment.class);
    }
}
