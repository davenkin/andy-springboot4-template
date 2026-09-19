package com.company.andy.feature.equipment.eventhandler;

import com.company.andy.common.event.consume.AbstractDomainEventHandler;
import com.company.andy.common.model.actor.PlatformActor;
import com.company.andy.common.utils.ExceptionSwallowRunner;
import com.company.andy.feature.equipment.domain.event.EquipmentDeletedEvent;
import com.company.andy.feature.maintenance.domain.task.DeleteAllMaintenanceRecordsUnderEquipmentTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EquipmentDeletedEventEventHandler extends AbstractDomainEventHandler<EquipmentDeletedEvent> {
    private final DeleteAllMaintenanceRecordsUnderEquipmentTask deleteAllMaintenanceRecordsUnderEquipmentTask;

    @Override
    protected void handle(EquipmentDeletedEvent event, PlatformActor actor) {
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
