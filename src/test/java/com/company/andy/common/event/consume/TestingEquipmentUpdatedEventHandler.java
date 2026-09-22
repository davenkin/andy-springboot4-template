package com.company.andy.common.event.consume;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
  private final List<HandledEvent> handledEvents = new CopyOnWriteArrayList<>();

  @Override
  protected void handle(EquipmentUpdatedEvent event, PlatformActor actor) {
    this.handledEvents.add(new HandledEvent(event, Instant.now()));
  }
}
