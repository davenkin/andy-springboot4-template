package com.company.andy.common.event.consume;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.company.andy.common.model.actor.SystemActor;
import com.company.andy.feature.equipment.domain.event.EquipmentHolderUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestingErrorNonTxEquipmentHolderUpdatedEventHandler extends AbstractEventHandler<EquipmentHolderUpdatedEvent> {
  public List<HandledEvent> handledEvents = new ArrayList<>();

  @Override
  protected void handle(EquipmentHolderUpdatedEvent event, SystemActor actor) {
    this.handledEvents.add(new HandledEvent(event, Instant.now()));
    throw new RuntimeException("Simulated error for event: " + event.getId());
  }

  @Override
  public boolean isTransactional() {
    return false;
  }
}
