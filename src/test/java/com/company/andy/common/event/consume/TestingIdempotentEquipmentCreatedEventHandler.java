package com.company.andy.common.event.consume;

import static java.util.Collections.synchronizedList;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.company.andy.common.model.actor.PlatformActor;
import com.company.andy.feature.equipment.domain.event.EquipmentCreatedEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Order(3)
@Component
@RequiredArgsConstructor
public class TestingIdempotentEquipmentCreatedEventHandler extends AbstractDomainEventHandler<EquipmentCreatedEvent> {
  private final List<HandledEvent> handledEvents = synchronizedList(new ArrayList<>());

  @Override
  protected void handle(EquipmentCreatedEvent event, PlatformActor actor) {
    synchronized (this.handledEvents) {
      this.handledEvents.add(new HandledEvent(event, Instant.now()));
    }
  }

  @Override
  public boolean isIdempotent() {
    return true;
  }
}
