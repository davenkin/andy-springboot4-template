package com.company.andy.feature.equipment.eventhandler;

import com.company.andy.common.event.consume.AbstractDomainEventHandler;
import com.company.andy.common.model.actor.PlatformActor;
import com.company.andy.feature.equipment.domain.event.EquipmentStatusUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// All DomainEvent handlers should extend from AbstractDomainEventHandler.

@Slf4j
@Component
@RequiredArgsConstructor
public class EquipmentStatusUpdatedEventHandler extends AbstractDomainEventHandler<EquipmentStatusUpdatedEvent> {

    @Override
    protected void handle(EquipmentStatusUpdatedEvent event, PlatformActor actor) {
        log.info("{} called for Equipment[{}].", this.getClass().getSimpleName(), event.getArId());
    }
}
