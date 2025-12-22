package com.company.andy.business.equipment.eventhandler;

import com.company.andy.business.equipment.domain.event.EquipmentUpdatedEvent;
import com.company.andy.common.event.consume.AbstractEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EquipmentUpdatedEventHandler extends AbstractEventHandler<EquipmentUpdatedEvent> {

    @Override
    public void handle(EquipmentUpdatedEvent event) {
        log.info("{} called for Equipment[{}].", this.getClass().getSimpleName(), event.getArId());
    }
}
