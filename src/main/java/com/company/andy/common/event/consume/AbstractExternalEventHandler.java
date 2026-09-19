package com.company.andy.common.event.consume;

import com.company.andy.common.event.consume.external.ExternalEvent;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.model.actor.ActorOrigin;
import com.company.andy.common.model.actor.PlatformActor;

// Base class for all external event handlers

public abstract class AbstractExternalEventHandler<T extends ExternalEvent> extends AbstractEventHandler<T> {

    @Override
    protected final PlatformActor getActor(T event) {
        ActorOrigin origin = ActorOrigin.fromEvent(event.getClass().getName(), event.getEventId());
        return Actor.createEventHandlerActor(event.getClass().getName(), origin);
    }
}
