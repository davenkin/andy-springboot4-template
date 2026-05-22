package com.company.andy.feature.equipment.domain;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Getter
@FieldNameConstants // For access field names
@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator)
public class EquipmentEngine {
  private String model;
  private int temperature;
  private boolean started;

  public EquipmentEngine(String model) {
    this.model = model;
    this.temperature = 0;
    this.started = false;
  }

  public void start() {
    this.started = true;
    this.temperature = 100;
  }

  public void stop() {
    this.started = false;
    this.temperature = 0;
  }
}
