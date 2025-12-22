package com.company.andy.feature.equipment.infrastructure;

import com.company.andy.common.infrastructure.AbstractMongoRepository;
import com.company.andy.common.model.AggregateRoot;
import com.company.andy.feature.equipment.domain.Equipment;
import com.company.andy.feature.equipment.domain.EquipmentSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import static com.company.andy.common.model.AggregateRoot.Fields.createdAt;
import static com.company.andy.common.util.CommonUtils.requireNonBlank;
import static com.company.andy.common.util.Constants.ORG_EQUIPMENTS_CACHE;
import static com.company.andy.feature.equipment.domain.Equipment.EQUIPMENT_COLLECTION;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.by;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CachedMongoEquipmentRepository extends AbstractMongoRepository<Equipment> {

    @Cacheable(value = ORG_EQUIPMENTS_CACHE, key = "#orgId")
    public CachedOrgEquipmentSummaries cachedEquipmentSummaries(String orgId) {
        requireNonBlank(orgId, "orgId must not be blank.");

        Query query = query(where(AggregateRoot.Fields.orgId).is(orgId)).with(by(ASC, createdAt));
        query.fields().include(AggregateRoot.Fields.orgId, Equipment.Fields.name, Equipment.Fields.status);
        return new CachedOrgEquipmentSummaries(mongoTemplate.find(query, EquipmentSummary.class, EQUIPMENT_COLLECTION));
    }

    @Caching(evict = {@CacheEvict(value = ORG_EQUIPMENTS_CACHE, key = "#orgId")})
    public void evictCachedEquipmentSummaries(String orgId) {
        requireNonBlank(orgId, "orgId must not be blank.");

        log.debug("Evicted cached equipment summaries for org[{}].", orgId);
    }
}
