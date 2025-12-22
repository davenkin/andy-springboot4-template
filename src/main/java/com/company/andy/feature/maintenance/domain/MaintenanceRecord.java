package com.company.andy.feature.maintenance.domain;

import com.company.andy.common.model.AggregateRoot;
import com.company.andy.common.model.operator.Operator;
import com.company.andy.feature.equipment.domain.EquipmentStatus;
import com.company.andy.feature.maintenance.domain.event.MaintenanceRecordCreatedEvent;
import com.company.andy.feature.maintenance.domain.event.MaintenanceRecordDeletedEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.company.andy.common.util.SnowflakeIdGenerator.newSnowflakeId;
import static com.company.andy.feature.maintenance.domain.MaintenanceRecord.MAINTENANCE_RECORD_COLLECTION;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Getter
@FieldNameConstants
@TypeAlias(MAINTENANCE_RECORD_COLLECTION)
@Document(MAINTENANCE_RECORD_COLLECTION)
@NoArgsConstructor(access = PRIVATE)
public class MaintenanceRecord extends AggregateRoot {
    public final static String MAINTENANCE_RECORD_COLLECTION = "maintenance-record";

    private String equipmentId;
    private String equipmentName;
    private EquipmentStatus status;
    private String description;

    public MaintenanceRecord(String equipmentId,
                             String equipmentName,
                             EquipmentStatus status,
                             String description,
                             Operator operator) {
        super(newMaintenanceRecordId(), operator);
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.status = status;
        this.description = description;
        raiseEvent(new MaintenanceRecordCreatedEvent(this));
    }

    public static String newMaintenanceRecordId() {
        return "MTR" + newSnowflakeId();
    }

    @Override
    public void onDelete() {
        raiseEvent(new MaintenanceRecordDeletedEvent(this));
    }
}
