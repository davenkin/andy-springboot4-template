package com.company.andy.common.event.consume;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.company.andy.common.model.actor.SystemActor;
import com.company.andy.feature.equipment.domain.event.EquipmentCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Order(2)
@Component
@RequiredArgsConstructor
public class TestingUrgentEquipmentCreatedEventHandler extends AbstractEventHandler<EquipmentCreatedEvent> {
  public List<HandledEvent> handledEvents = new ArrayList<>();

  @Override
  protected void handle(EquipmentCreatedEvent event, SystemActor actor) {
    this.handledEvents.add(new HandledEvent(event, Instant.now()));
  }

  @Override
  public int priority() {
    return -1; // make it urgent
  }
}
