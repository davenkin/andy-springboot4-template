package com.company.andy.feature.equipment.eventhandler;

import com.company.andy.common.event.consume.AbstractDomainEventHandler;
import com.company.andy.common.model.actor.PlatformActor;
import com.company.andy.feature.equipment.domain.event.EquipmentUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EquipmentUpdatedEventHandler extends AbstractDomainEventHandler<EquipmentUpdatedEvent> {

    @Override
    protected void handle(EquipmentUpdatedEvent event, PlatformActor actor) {
        log.info("{} called for Equipment[{}].", this.getClass().getSimpleName(), event.getArId());
    }
}
