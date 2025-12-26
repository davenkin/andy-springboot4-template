package com.company.andy.feature.equipment.eventhandler;

import com.company.andy.common.event.consume.AbstractEventHandler;
import com.company.andy.common.util.ExceptionSwallowRunner;
import com.company.andy.feature.equipment.domain.event.EquipmentNameUpdatedEvent;
import com.company.andy.feature.equipment.domain.task.SyncEquipmentNameToMaintenanceRecordsTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EquipmentNameUpdatedEventHandler extends AbstractEventHandler<EquipmentNameUpdatedEvent> {
    private final SyncEquipmentNameToMaintenanceRecordsTask syncEquipmentNameToMaintenanceRecordsTask;

    @Override
    public void handle(EquipmentNameUpdatedEvent event) {
        ExceptionSwallowRunner.run(() -> syncEquipmentNameToMaintenanceRecordsTask.run(event.getEquipmentId()));
    }
}
