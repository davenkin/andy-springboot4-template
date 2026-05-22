package com.company.andy.feature.org.equipment.eventhandler.external;

import com.company.andy.common.event.consume.AbstractEventHandler;
import com.company.andy.common.model.actor.SystemActor;
import com.company.andy.feature.org.equipment.domain.Equipment;
import com.company.andy.feature.org.equipment.domain.EquipmentFactory;
import com.company.andy.feature.org.equipment.domain.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalEquipmentInstalledEventHandler extends AbstractEventHandler<ExternalEquipmentInstalledEvent> {
  private final EquipmentRepository equipmentRepository;
  private final EquipmentFactory equipmentFactory;

  @Override
  protected void handle(ExternalEquipmentInstalledEvent event, SystemActor actor) {
    Equipment equipment = equipmentFactory.create(event.getEquipmentId(), event.getName(), event.getOrgId(), event.getEngine(), actor);
    equipmentRepository.save(equipment);
  }
}
