package com.company.andy.sample.equipment.domain.event;

import com.company.andy.common.event.DomainEvent;
import com.company.andy.sample.equipment.domain.Equipment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;

import static com.company.andy.common.event.DomainEventType.EQUIPMENT_DELETED_EVENT;
import static lombok.AccessLevel.PRIVATE;

@Getter
@TypeAlias("EQUIPMENT_DELETED_EVENT")
@NoArgsConstructor(access = PRIVATE)
public class EquipmentDeletedEvent extends DomainEvent {
    private String equipmentId;

    public EquipmentDeletedEvent(Equipment equipment) {
        super(EQUIPMENT_DELETED_EVENT, equipment);
        this.equipmentId = equipment.getId();
    }
}
