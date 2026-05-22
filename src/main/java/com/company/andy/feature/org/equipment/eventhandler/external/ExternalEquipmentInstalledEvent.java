package com.company.andy.feature.org.equipment.eventhandler.external;

import static lombok.AccessLevel.PRIVATE;

import com.company.andy.common.event.external.ExternalEvent;
import com.company.andy.feature.org.equipment.domain.EquipmentEngine;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator)
public class ExternalEquipmentInstalledEvent extends ExternalEvent {
  private String equipmentId;
  private String orgId;
  private String name;
  private EquipmentEngine engine;
}
