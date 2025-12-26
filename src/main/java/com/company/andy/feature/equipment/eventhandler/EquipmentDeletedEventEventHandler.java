package com.company.andy.feature.equipment.eventhandler;

import com.company.andy.common.event.consume.AbstractEventHandler;
import com.company.andy.common.util.ExceptionSwallowRunner;
import com.company.andy.feature.equipment.domain.event.EquipmentDeletedEvent;
import com.company.andy.feature.maintenance.domain.task.DeleteAllMaintenanceRecordsUnderEquipmentTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EquipmentDeletedEventEventHandler extends AbstractEventHandler<EquipmentDeletedEvent> {
    private final DeleteAllMaintenanceRecordsUnderEquipmentTask deleteAllMaintenanceRecordsUnderEquipmentTask;

    @Override
    public void handle(EquipmentDeletedEvent event) {
        ExceptionSwallowRunner.run(() -> deleteAllMaintenanceRecordsUnderEquipmentTask.run(event.getEquipmentId()));
    }

    @Override
    public boolean isIdempotent() {
        return true;// This handler can run multiple times safely
    }

    @Override
    public boolean isTransactional() {
        return false; // Better not be transactional as it deletes multiple records which can exceed Mongo transaction restrictions
    }
}
