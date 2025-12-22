package com.company.andy.business.equipment.eventhandler;

import com.company.andy.business.equipment.domain.event.EquipmentCreatedEvent;
import com.company.andy.common.event.consume.AbstractEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EquipmentCreatedEventHandler2 extends AbstractEventHandler<EquipmentCreatedEvent> {
    @Override
    public void handle(EquipmentCreatedEvent event) {
        log.info("{} called for Equipment[{}].", this.getClass().getSimpleName(), event.getArId());
    }
}
