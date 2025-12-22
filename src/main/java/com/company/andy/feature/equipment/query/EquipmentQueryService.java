package com.company.andy.feature.equipment.query;

import com.company.andy.common.model.AggregateRoot;
import com.company.andy.common.model.operator.Operator;
import com.company.andy.common.util.PagedResponse;
import com.company.andy.feature.equipment.domain.Equipment;
import com.company.andy.feature.equipment.domain.EquipmentRepository;
import com.company.andy.feature.equipment.domain.EquipmentSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.company.andy.feature.equipment.domain.Equipment.EQUIPMENT_COLLECTION;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Component
@RequiredArgsConstructor
public class EquipmentQueryService {
    private final MongoTemplate mongoTemplate;
    private final EquipmentRepository equipmentRepository;

    public PagedResponse<QPagedEquipment> pageEquipments(PageEquipmentsQuery query, Operator operator) {
        Criteria criteria = where(AggregateRoot.Fields.orgId).is(operator.getOrgId());

        if (isNotBlank(query.getSearch())) {
            criteria.and(Equipment.Fields.name).regex(query.getSearch());
        }

        if (query.getStatus() != null) {
            criteria.and(Equipment.Fields.status).is(query.getStatus());
        }

        Query mongoQuery = Query.query(criteria);
        mongoQuery.fields().include(AggregateRoot.Fields.orgId,
                Equipment.Fields.name,
                Equipment.Fields.status,
                AggregateRoot.Fields.createdAt,
                AggregateRoot.Fields.createdBy);

        Pageable pageable = query.pageable();
        long count = mongoTemplate.count(mongoQuery, Equipment.class);
        if (count == 0) {
            return PagedResponse.empty(pageable);
        }

        List<QPagedEquipment> equipments = mongoTemplate.find(mongoQuery.with(pageable), QPagedEquipment.class, EQUIPMENT_COLLECTION);
        return new PagedResponse<>(equipments, pageable, count);
    }

    public QDetailedEquipment getEquipmentDetail(String equipmentId, Operator operator) {
        Equipment equipment = equipmentRepository.byId(equipmentId, operator.getOrgId());
        return QDetailedEquipment.builder()
                .id(equipment.getId())
                .orgId(equipment.getOrgId())
                .name(equipment.getName())
                .status(equipment.getStatus())
                .createdAt(equipment.getCreatedAt())
                .createdBy(equipment.getCreatedBy())
                .build();
    }

    public List<EquipmentSummary> getAllEquipmentSummaries(Operator operator) {
        return equipmentRepository.cachedEquipmentSummaries(operator.getOrgId());
    }
}
