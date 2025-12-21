package com.company.andy.sample.equipment.domain.event;

import com.company.andy.common.event.DomainEvent;
import com.company.andy.common.event.DomainEventType;
import com.company.andy.sample.equipment.domain.Equipment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
public abstract class EquipmentUpdatedEvent extends DomainEvent {
    private String equipmentId;

    public EquipmentUpdatedEvent(DomainEventType type, Equipment equipment) {
        super(type, equipment);
        this.equipmentId = equipment.getId();
    }
}
