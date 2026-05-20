package com.company.andy.feature.org.equipment.domain;

import com.company.andy.common.exception.ServiceException;
import com.company.andy.common.model.AggregateRoot;
import com.company.andy.common.model.actor.Actor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.company.andy.common.exception.ErrorCode.EQUIPMENT_NAME_ALREADY_EXISTS;
import static com.company.andy.common.util.NullableMapUtils.mapOf;

// Domain services handles domain logic that's not suitable to be put inside aggregate root

@Component
@RequiredArgsConstructor
public class EquipmentDomainService {
    private final EquipmentRepository equipmentRepository;

    public void updateEquipmentName(Equipment equipment, String newName, Actor actor) {
        if (!Objects.equals(newName, equipment.getName()) &&
            equipmentRepository.existsByName(newName, equipment.getOrgId())) {
            throw new ServiceException(EQUIPMENT_NAME_ALREADY_EXISTS,
                    "Equipment Name Already Exists.",
                    mapOf(AggregateRoot.Fields.id, equipment.getId(), Equipment.Fields.name, newName));
        }

        equipment.updateName(newName, actor);
    }
}
