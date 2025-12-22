package com.company.andy.business.equipment.eventhandler;

import com.company.andy.business.equipment.domain.EquipmentRepository;
import com.company.andy.business.equipment.domain.event.EquipmentNameUpdatedEvent;
import com.company.andy.business.equipment.domain.task.SyncEquipmentNameToMaintenanceRecordsTask;
import com.company.andy.common.event.consume.AbstractEventHandler;
import com.company.andy.common.util.ExceptionSwallowRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EquipmentNameUpdatedEventHandler extends AbstractEventHandler<EquipmentNameUpdatedEvent> {
    private final EquipmentRepository equipmentRepository;
    private final SyncEquipmentNameToMaintenanceRecordsTask syncEquipmentNameToMaintenanceRecordsTask;

    @Override
    public void handle(EquipmentNameUpdatedEvent event) {
        ExceptionSwallowRunner.run(() -> equipmentRepository.evictCachedEquipmentSummaries(event.getArOrgId()));
        ExceptionSwallowRunner.run(() -> syncEquipmentNameToMaintenanceRecordsTask.run(event.getEquipmentId()));
    }
}
