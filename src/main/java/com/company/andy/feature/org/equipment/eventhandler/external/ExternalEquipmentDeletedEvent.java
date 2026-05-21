package com.company.andy.feature.org.equipment.eventhandler.external;

import com.company.andy.common.model.event.ExternalEvent;
import lombok.Getter;

@Getter
public class ExternalEquipmentDeletedEvent extends ExternalEvent {
    private String equipmentId;
}
