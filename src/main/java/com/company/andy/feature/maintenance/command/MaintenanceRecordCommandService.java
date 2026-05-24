package com.company.andy.feature.maintenance.command;

import com.company.andy.common.model.actor.OrgActor;
import com.company.andy.feature.equipment.domain.Equipment;
import com.company.andy.feature.equipment.domain.EquipmentRepository;
import com.company.andy.feature.maintenance.domain.MaintenanceRecord;
import com.company.andy.feature.maintenance.domain.MaintenanceRecordFactory;
import com.company.andy.feature.maintenance.domain.MaintenanceRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// Command services handles command objects and orchestrates the processing flow
// Command services should not contain business logic but delegate to Aggregate Roots or domain services

@Slf4j
@Component
@RequiredArgsConstructor
public class MaintenanceRecordCommandService {
    private final MaintenanceRecordFactory maintenanceRecordFactory;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final EquipmentRepository equipmentRepository;

    @Transactional
    public String createMaintenanceRecord(CreateMaintenanceRecordCommand command, OrgActor actor) {
        Equipment equipment = equipmentRepository.byId(command.equipmentId(), actor.getOrgId());
        MaintenanceRecord record = maintenanceRecordFactory.create(equipment,
                command.status(),
                command.description(),
                actor);
        maintenanceRecordRepository.save(record);
        log.info("Created MaintenanceRecord[{}].", record.getId());
        return record.getId();
    }

    @Transactional
    public void deleteMaintenanceRecord(String maintenanceRecordId, OrgActor actor) {
        MaintenanceRecord maintenanceRecord = maintenanceRecordRepository.byId(maintenanceRecordId, actor.getOrgId());
        maintenanceRecord.onDelete(actor);
        maintenanceRecordRepository.delete(maintenanceRecord);
        log.info("Deleted MaintenanceRecord[{}].", maintenanceRecordId);
    }
}
