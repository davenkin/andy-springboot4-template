package com.company.andy.sample.equipment.domain.event;

import com.company.andy.sample.equipment.domain.Equipment;
import com.company.andy.sample.equipment.domain.EquipmentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;

import static com.company.andy.common.event.DomainEventType.EQUIPMENT_STATUS_UPDATED_EVENT;
import static lombok.AccessLevel.PRIVATE;

@Getter
@TypeAlias("EQUIPMENT_STATUS_UPDATED_EVENT")
@NoArgsConstructor(access = PRIVATE)
public class EquipmentStatusUpdatedEvent extends EquipmentUpdatedEvent {
    private EquipmentStatus status;

    public EquipmentStatusUpdatedEvent(EquipmentStatus status, Equipment equipment) {
        super(EQUIPMENT_STATUS_UPDATED_EVENT, equipment);
        this.status = status;
    }
}
