package com.company.andy.common.event.consume;

import com.company.andy.common.event.DomainEvent;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.model.actor.ActorOrigin;
import com.company.andy.common.model.actor.PlatformActor;

// Base class for all DomainEvent handlers

public abstract class AbstractDomainEventHandler<T extends DomainEvent> extends AbstractEventHandler<T> {

    @Override
    protected final PlatformActor getActor(T event) {
        ActorOrigin origin = ActorOrigin.fromEvent(event.getClass().getName(), event.getId());
        return Actor.createEventHandlerActor(event.getClass().getName(), origin);
    }
}
