package com.company.andy.feature.maintenance.eventhandler;

import com.company.andy.common.event.consume.AbstractEventHandler;
import com.company.andy.common.model.actor.SystemActor;
import com.company.andy.common.utils.ExceptionSwallowRunner;
import com.company.andy.feature.equipment.domain.EquipmentRepository;
import com.company.andy.feature.equipment.domain.task.CountMaintenanceRecordsForEquipmentTask;
import com.company.andy.feature.maintenance.domain.MaintenanceRecordRepository;
import com.company.andy.feature.maintenance.domain.event.MaintenanceRecordCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MaintenanceRecordCreatedEventHandler extends AbstractEventHandler<MaintenanceRecordCreatedEvent> {
    private final CountMaintenanceRecordsForEquipmentTask countMaintenanceRecordsForEquipmentTask;
    private final EquipmentRepository equipmentRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;

    @Override
    protected void handle(MaintenanceRecordCreatedEvent event, SystemActor actor) {
        ExceptionSwallowRunner.run(() -> countMaintenanceRecordsForEquipmentTask.run(event.getEquipmentId()));
        ExceptionSwallowRunner.run(() -> updateEquipmentStatus(event.getEquipmentId(), actor));
    }

    private void updateEquipmentStatus(String equipmentId, SystemActor actor) {
        equipmentRepository.byIdOptional(equipmentId).ifPresent(equipment -> {
            maintenanceRecordRepository.latestForOptional(equipmentId).ifPresent(record -> {
                equipment.updateStatus(record.getStatus(), actor);
                equipmentRepository.save(equipment);
                log.info("Updated equipment[{}] status from its lasted maintenance record[{}].",
                        equipment.getId(), record.getId());
            });
        });
    }

    @Override
    public boolean isIdempotent() {
        // This handler can run multiple times safely
        return true;
    }
}
