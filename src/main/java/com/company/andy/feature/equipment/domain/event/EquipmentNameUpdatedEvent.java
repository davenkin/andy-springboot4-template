package com.company.andy.feature.equipment.domain.event;

import com.company.andy.feature.equipment.domain.Equipment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;

import static com.company.andy.common.event.DomainEventType.EQUIPMENT_NAME_UPDATED_EVENT;
import static lombok.AccessLevel.PRIVATE;

@Getter
@TypeAlias("EQUIPMENT_NAME_UPDATED_EVENT")
@NoArgsConstructor(access = PRIVATE)
public class EquipmentNameUpdatedEvent extends EquipmentUpdatedEvent {
    private String updatedName;

    public EquipmentNameUpdatedEvent(String updatedName, Equipment equipment) {
        super(EQUIPMENT_NAME_UPDATED_EVENT, equipment);
        this.updatedName = updatedName;
    }
}
