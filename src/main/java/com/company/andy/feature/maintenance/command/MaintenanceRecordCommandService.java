package com.company.andy.feature.maintenance.command;

import com.company.andy.feature.equipment.domain.Equipment;
import com.company.andy.feature.equipment.domain.EquipmentRepository;
import com.company.andy.feature.maintenance.domain.MaintenanceRecord;
import com.company.andy.feature.maintenance.domain.MaintenanceRecordFactory;
import com.company.andy.feature.maintenance.domain.MaintenanceRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// CommandService handles Command objects and orchestrates the processing flow.
// CommandService should not contain business logic but delegate to AggregateRoot or DomainService.

@Slf4j
@Component
@RequiredArgsConstructor
public class MaintenanceRecordCommandService {
    private final MaintenanceRecordFactory maintenanceRecordFactory;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final EquipmentRepository equipmentRepository;

    @Transactional
    public String createMaintenanceRecord(CreateMaintenanceRecordCommand command, String orgId) {
        Equipment equipment = equipmentRepository.byId(command.equipmentId(), orgId);
        MaintenanceRecord record = maintenanceRecordFactory.create(equipment, command.status(), command.description());
        maintenanceRecordRepository.save(record);
        log.info("Created MaintenanceRecord[{}].", record.getId());
        return record.getId();
    }

    @Transactional
    public void deleteMaintenanceRecord(String maintenanceRecordId, String orgId) {
        MaintenanceRecord maintenanceRecord = maintenanceRecordRepository.byId(maintenanceRecordId, orgId);
        maintenanceRecord.onDelete();
        maintenanceRecordRepository.delete(maintenanceRecord);
        log.info("Deleted MaintenanceRecord[{}].", maintenanceRecordId);
    }
}
