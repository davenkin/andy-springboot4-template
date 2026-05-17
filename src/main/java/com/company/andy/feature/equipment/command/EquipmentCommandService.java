package com.company.andy.feature.equipment.command;

import com.company.andy.common.model.actor.Actor;
import com.company.andy.feature.equipment.domain.Equipment;
import com.company.andy.feature.equipment.domain.EquipmentDomainService;
import com.company.andy.feature.equipment.domain.EquipmentFactory;
import com.company.andy.feature.equipment.domain.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// Command services handles command objects and orchestrates the processing flow
// Command services should not contain business logic but delegate to aggregate roots or domain services

@Slf4j
@Component
@RequiredArgsConstructor
public class EquipmentCommandService {
    private final EquipmentRepository equipmentRepository;
    private final EquipmentFactory equipmentFactory;
    private final EquipmentDomainService equipmentDomainService;

    @Transactional
    public String createEquipment(CreateEquipmentCommand command, Actor actor) {
        Equipment equipment = equipmentFactory.create(command.name(), actor);
        equipmentRepository.save(equipment);
        log.info("Created Equipment[{}].", equipment.getId());
        return equipment.getId();
    }

    @Transactional
    public void updateEquipmentName(String equipmentId, UpdateEquipmentNameCommand command, Actor actor) {
        Equipment equipment = equipmentRepository.byId(equipmentId, actor.orgId());
        equipmentDomainService.updateEquipmentName(equipment, command.name(), actor);
        equipmentRepository.save(equipment);
        log.info("Updated name for Equipment[{}].", equipment.getId());
    }

    @Transactional
    public void updateEquipmentHolder(String equipmentId, UpdateEquipmentHolderCommand command, Actor actor) {
        Equipment equipment = equipmentRepository.byId(equipmentId, actor.orgId());
        equipment.updateHolder(command.name(), actor);
        equipmentRepository.save(equipment);
        log.info("Updated holder for Equipment[{}].", equipment.getId());
    }

    @Transactional
    public void deleteEquipment(String equipmentId, Actor actor) {
        Equipment equipment = equipmentRepository.byId(equipmentId, actor.orgId());
        equipment.onDelete(actor);
        equipmentRepository.delete(equipment);
        log.info("Deleted Equipment[{}].", equipmentId);
    }
}
