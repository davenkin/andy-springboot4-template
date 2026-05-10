package com.company.andy.feature.equipment.eventhandler;

import com.company.andy.common.event.consume.AbstractEventHandler;
import com.company.andy.feature.equipment.domain.event.EquipmentCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EquipmentCreatedEventHandler extends AbstractEventHandler<EquipmentCreatedEvent> {
    private final TaskExecutor taskExecutor;

    @Override
    public void handle(EquipmentCreatedEvent event) {
        log.info("EquipmentCreatedEvent received for Equipment[{}].", event.getEquipmentId());
        taskExecutor.execute(() -> {
            log.info("{} called for Equipment[{}].", this.getClass().getSimpleName(), event.getArId());
        });
        // imple
    }
}
