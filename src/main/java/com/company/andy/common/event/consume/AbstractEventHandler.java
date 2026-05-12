package com.company.andy.common.event.consume;

import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.tracing.ActorMdcSupport;

import static com.company.andy.common.model.actor.Actor.createPlatformActor;
import static com.company.andy.common.model.actor.ActorType.EVENT_LISTENER;
import static com.company.andy.common.util.CommonUtils.singleParameterizedArgumentClassOf;

// All event handlers should extend this class
public abstract class AbstractEventHandler<T> {
    private final Class<?> eventClass;

    protected AbstractEventHandler() {
        this.eventClass = singleParameterizedArgumentClassOf(this.getClass());
    }

    public boolean isIdempotent() {
        return false; // By default, all handlers are assumed to be not idempotent by themselves
    }

    public boolean isTransactional() {
        return true; // By default, all handlers are assumed to be transactional, we should make handlers to be transactional as much as possible
    }

    public int priority() {
        return 0; // Smaller value means higher priority and will be handled first
    }

    public final String getName() {
        return this.getClass().getName();
    }

    public final boolean canHandle(Object event) {
        return this.eventClass.isAssignableFrom(event.getClass());
    }

    public final void handle(T event) {
        Actor actor = createPlatformActor(EVENT_LISTENER, event.getClass().getName());
        ActorMdcSupport.runWithMdc(actor, () -> this.handle(event, actor));
    }

    protected abstract void handle(T event, Actor actor);
}
