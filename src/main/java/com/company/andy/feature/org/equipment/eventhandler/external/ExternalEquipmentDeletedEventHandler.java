package com.company.andy.feature.org.equipment.eventhandler.external;

import com.company.andy.common.event.consume.AbstractEventHandler;
import com.company.andy.common.model.actor.SystemActor;
import com.company.andy.feature.org.equipment.domain.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalEquipmentDeletedEventHandler extends AbstractEventHandler<ExternalEquipmentDeletedEvent> {
    private final EquipmentRepository equipmentRepository;

    @Override
    protected void handle(ExternalEquipmentDeletedEvent event, SystemActor actor) {
        equipmentRepository.byIdOptional(event.getEquipmentId()).ifPresent(equipment -> {
            equipment.onDelete(actor);
            equipmentRepository.delete(equipment);
        });
    }
}
