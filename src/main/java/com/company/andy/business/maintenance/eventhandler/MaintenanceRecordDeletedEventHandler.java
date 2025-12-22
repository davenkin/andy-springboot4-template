package com.company.andy.business.maintenance.eventhandler;

import com.company.andy.business.equipment.domain.task.CountMaintenanceRecordsForEquipmentTask;
import com.company.andy.business.maintenance.domain.event.MaintenanceRecordDeletedEvent;
import com.company.andy.common.event.consume.AbstractEventHandler;
import com.company.andy.common.util.ExceptionSwallowRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MaintenanceRecordDeletedEventHandler extends AbstractEventHandler<MaintenanceRecordDeletedEvent> {
    private final CountMaintenanceRecordsForEquipmentTask countMaintenanceRecordsForEquipmentTask;

    @Override
    public void handle(MaintenanceRecordDeletedEvent event) {
        ExceptionSwallowRunner.run(() -> countMaintenanceRecordsForEquipmentTask.run(event.getEquipmentId()));
    }
}
