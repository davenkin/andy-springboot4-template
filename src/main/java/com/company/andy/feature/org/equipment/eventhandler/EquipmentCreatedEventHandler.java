package com.company.andy.feature.org.equipment.eventhandler;

import com.company.andy.common.event.consume.AbstractEventHandler;
import com.company.andy.common.model.actor.SystemActor;
import com.company.andy.feature.org.equipment.domain.event.EquipmentCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EquipmentCreatedEventHandler extends AbstractEventHandler<EquipmentCreatedEvent> {

    @Override
    protected void handle(EquipmentCreatedEvent event, SystemActor actor) {
        log.info("{} called for Equipment[{}].", this.getClass().getSimpleName(), event.getArId());
    }
}
