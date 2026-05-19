package com.company.andy.common.event.consume;

import static com.company.andy.common.model.actor.SystemActor.createEventListenerSystemActor;
import static com.company.andy.common.util.CommonUtils.singleParameterizedArgumentClassOf;

import com.company.andy.common.model.actor.SystemActor;
import com.company.andy.common.tracing.ActorMdcSupport;

// Base class for all event handlers, deals with handlers' priority, idempotency and transactionality

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
    SystemActor actor = createEventListenerSystemActor(event.getClass().getName());
    ActorMdcSupport.runWithMdc(actor, () -> this.handle(event, actor));
  }

  protected abstract void handle(T event, SystemActor actor);
}
