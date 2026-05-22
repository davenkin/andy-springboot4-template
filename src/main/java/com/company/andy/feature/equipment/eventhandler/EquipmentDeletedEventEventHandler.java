package com.company.andy.feature.equipment.eventhandler;

import com.company.andy.common.event.consume.AbstractEventHandler;
import com.company.andy.common.model.actor.SystemActor;
import com.company.andy.common.utils.ExceptionSwallowRunner;
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
    protected void handle(EquipmentDeletedEvent event, SystemActor actor) {
        ExceptionSwallowRunner.run(() -> deleteAllMaintenanceRecordsUnderEquipmentTask.run(event.getEquipmentId()));
    }

    @Override
    public boolean isIdempotent() {
        // This handler can run multiple times safely
        return true;
    }

    @Override
    public boolean isTransactional() {
        // Not transactional as it deletes multiple records which can exceed Mongo's transaction restrictions
        return false;
    }
}
