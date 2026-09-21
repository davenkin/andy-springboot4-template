package com.company.andy.common.event.consume;

import static java.util.Collections.synchronizedList;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.company.andy.common.model.actor.PlatformActor;
import com.company.andy.feature.equipment.domain.event.EquipmentUpdatedEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class TestingEquipmentUpdatedEventHandler extends AbstractDomainEventHandler<EquipmentUpdatedEvent> {
  private final List<HandledEvent> handledEvents = synchronizedList(new ArrayList<>());

  @Override
  protected void handle(EquipmentUpdatedEvent event, PlatformActor actor) {
    synchronized (this.handledEvents) {
      this.handledEvents.add(new HandledEvent(event, Instant.now()));
    }
  }
}
