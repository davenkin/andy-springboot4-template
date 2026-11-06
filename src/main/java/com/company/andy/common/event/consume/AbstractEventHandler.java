package com.company.andy.common.event.consume;

import static com.company.andy.common.utils.CommonUtils.singleParameterizedArgumentClassOf;

// Base class for all event handlers, deals with handlers' priority, idempotency and transactionality.

public abstract class AbstractEventHandler<T> {
  private final Class<T> targetEventClass;

  @SuppressWarnings("unchecked")
  protected AbstractEventHandler() {
    this.targetEventClass = (Class<T>) singleParameterizedArgumentClassOf(this.getClass());
  }

  public boolean isIdempotent() {
    // By default, all handlers are assumed to be not idempotent by themselves
    return false;
  }

  public boolean isTransactional() {
    // By default, all handlers are assumed to be transactional, we should make handlers to be transactional as much as possible
    return true;
  }

  public int priority() {
    // Smaller value means higher priority and will be handled first
    return 0;
  }

  public final String getName() {
    return this.getClass().getName();
  }

  public final boolean canHandle(Object event) {
    return this.targetEventClass.isAssignableFrom(event.getClass());
  }

  public final void handleEventObject(Object event) {
    if (canHandle(event)) {
      handle(this.targetEventClass.cast(event));
    } else {
      throw new UnsupportedOperationException("Cannot handle event object of type [" + event.getClass().getName() + "].");
    }
  }

  public abstract void handle(T event);
}
