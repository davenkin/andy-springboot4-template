package com.company.andy.feature.equipment.domain.event;

import static lombok.AccessLevel.PROTECTED;

import com.company.andy.common.event.DomainEvent;
import com.company.andy.common.event.DomainEventType;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.feature.equipment.domain.Equipment;
import lombok.Getter;
import lombok.NoArgsConstructor;

// Parent class for all equipment update events

@Getter
@NoArgsConstructor(access = PROTECTED)
public abstract class EquipmentUpdatedEvent extends DomainEvent {
  private String equipmentId;

  protected EquipmentUpdatedEvent(DomainEventType type, Equipment equipment, Actor actor) {
    super(type, equipment, actor);
    this.equipmentId = equipment.getId();
  }
}
