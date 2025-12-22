package com.company.andy.feature.equipment.domain;

import com.company.andy.common.model.AggregateRoot;
import com.company.andy.common.model.operator.Operator;
import com.company.andy.feature.equipment.domain.event.EquipmentCreatedEvent;
import com.company.andy.feature.equipment.domain.event.EquipmentDeletedEvent;
import com.company.andy.feature.equipment.domain.event.EquipmentNameUpdatedEvent;
import com.company.andy.feature.equipment.domain.event.EquipmentStatusUpdatedEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

import static com.company.andy.common.util.SnowflakeIdGenerator.newSnowflakeId;
import static com.company.andy.feature.equipment.domain.Equipment.EQUIPMENT_COLLECTION;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Getter
@FieldNameConstants
@TypeAlias(EQUIPMENT_COLLECTION)
@Document(EQUIPMENT_COLLECTION)
@NoArgsConstructor(access = PRIVATE)
public class Equipment extends AggregateRoot {
    public final static String EQUIPMENT_COLLECTION = "equipment";
    private String name;
    private EquipmentStatus status;
    private String holder;
    private long maintenanceRecordCount;

    public Equipment(String name, Operator operator) {
        super(newEquipmentId(), operator);
        this.name = name;
        raiseEvent(new EquipmentCreatedEvent(this));
    }

    public static String newEquipmentId() {
        return "EQP" + newSnowflakeId(); // Generate ID in the code
    }

    public void updateName(String newName) {
        if (Objects.equals(newName, this.name)) {
            return;
        }
        this.name = newName;
        raiseEvent(new EquipmentNameUpdatedEvent(name, this));
    }

    public void updateHolder(String newHolder) {
        this.holder = newHolder;
    }

    public void updateStatus(EquipmentStatus status) {
        if (this.status == status) {
            return;
        }
        this.status = status;
        raiseEvent(new EquipmentStatusUpdatedEvent(this.status, this));

    }

    @Override
    public void onDelete() {
        raiseEvent(new EquipmentDeletedEvent(this));
    }
}
