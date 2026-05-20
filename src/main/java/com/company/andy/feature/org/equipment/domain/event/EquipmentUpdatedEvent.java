package com.company.andy.feature.org.equipment.domain.event;

import com.company.andy.common.event.DomainEvent;
import com.company.andy.common.event.DomainEventType;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.feature.org.equipment.domain.Equipment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

// Parent class for all equipment update events

@Getter
@NoArgsConstructor(access = PROTECTED)
public abstract class EquipmentUpdatedEvent extends DomainEvent {
    private String equipmentId;

    public EquipmentUpdatedEvent(DomainEventType type, Equipment equipment, Actor actor) {
        super(type, equipment, actor);
        this.equipmentId = equipment.getId();
    }
}
