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
    public void updateEquipmentName(String id, UpdateEquipmentNameCommand command, Actor actor) {
        Equipment equipment = equipmentRepository.byId(id, actor.orgId());
        equipmentDomainService.updateEquipmentName(equipment, command.name(), actor);
        equipmentRepository.save(equipment);
        log.info("Updated name for Equipment[{}].", equipment.getId());
    }

    @Transactional
    public void updateEquipmentHolder(String id, UpdateEquipmentHolderCommand command, Actor actor) {
        Equipment equipment = equipmentRepository.byId(id, actor.orgId());
        equipment.updateHolder(command.name());
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
