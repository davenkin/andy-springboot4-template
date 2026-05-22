package com.company.andy.feature.equipment.domain.event;

import static com.company.andy.common.event.DomainEventType.EQUIPMENT_DELETED_EVENT;
import static lombok.AccessLevel.PRIVATE;

import com.company.andy.common.event.DomainEvent;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.feature.equipment.domain.Equipment;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;

@Getter
@TypeAlias("EQUIPMENT_DELETED_EVENT")
@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator)
public class EquipmentDeletedEvent extends DomainEvent {
  private String equipmentId;

  public EquipmentDeletedEvent(Equipment equipment, Actor actor) {
    super(EQUIPMENT_DELETED_EVENT, equipment, actor);
    this.equipmentId = equipment.getId();
  }
}
