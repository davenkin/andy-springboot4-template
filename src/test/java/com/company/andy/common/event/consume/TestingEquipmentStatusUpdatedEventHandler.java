package com.company.andy.common.event.consume;

import com.company.andy.common.model.actor.SystemActor;
import com.company.andy.feature.equipment.domain.event.EquipmentStatusUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestingEquipmentStatusUpdatedEventHandler extends AbstractEventHandler<EquipmentStatusUpdatedEvent> {
    public List<HandledEvent> handledEvents = new ArrayList<>();

    @Override
    protected void handle(EquipmentStatusUpdatedEvent event, SystemActor actor) {
        this.handledEvents.add(new HandledEvent(event, Instant.now()));
    }
}
