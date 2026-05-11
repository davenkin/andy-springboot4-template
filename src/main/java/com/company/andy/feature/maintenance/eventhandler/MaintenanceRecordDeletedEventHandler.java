package com.company.andy.feature.maintenance.eventhandler;

import com.company.andy.common.event.consume.AbstractEventHandler;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.util.ExceptionSwallowRunner;
import com.company.andy.feature.equipment.domain.task.CountMaintenanceRecordsForEquipmentTask;
import com.company.andy.feature.maintenance.domain.event.MaintenanceRecordDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MaintenanceRecordDeletedEventHandler extends AbstractEventHandler<MaintenanceRecordDeletedEvent> {
    private final CountMaintenanceRecordsForEquipmentTask countMaintenanceRecordsForEquipmentTask;

    @Override
    protected void handle(MaintenanceRecordDeletedEvent event, Actor actor) {
        ExceptionSwallowRunner.run(() -> countMaintenanceRecordsForEquipmentTask.run(event.getEquipmentId()));
    }
}
