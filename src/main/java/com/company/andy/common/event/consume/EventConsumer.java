package com.company.andy.common.event.consume;

import com.company.andy.common.event.DomainEvent;
import com.company.andy.common.model.operator.Operator;
import com.company.andy.common.tracing.ActorMdcSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.company.andy.common.model.operator.Operator.createPlatformOperator;
import static com.company.andy.common.model.operator.OperatorSource.EVENT_LISTENER;
import static java.util.Comparator.comparingInt;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

// This class is the main place where events consuming orchestration happens
@Slf4j
@Component
public class EventConsumer {
    private final List<AbstractEventHandler<?>> handlers;
    private final ConsumingEventDao consumingEventDao;
    private final TransactionTemplate transactionTemplate;

    public EventConsumer(List<AbstractEventHandler<?>> handlers,
                         ConsumingEventDao consumingEventDao,
                         PlatformTransactionManager transactionManager) {
        this.handlers = handlers;
        this.consumingEventDao = consumingEventDao;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public void consumeDomainEvent(DomainEvent event) {
        this.consume(new ConsumingEvent(event.getId(), event));
    }

    // you may add more consumeXxxEvent(XxxEvent event) here, inside the method call consume(ConsumingEvent event)

    private void consume(ConsumingEvent event) {
        if (event == null) {
            return;
        }

        log.debug("Start consume event[{}:{}].", event.getType(), event.getEventId());
        Set<String> errorHandlers = new HashSet<>();
        this.handlers.stream()
                .filter(handler -> handler.canHandle(event.getEvent()))
                .sorted(comparingInt(AbstractEventHandler::priority))
                .forEach(handler -> {
                    try {
                        if (handler.isTransactional()) {
                            this.transactionTemplate.executeWithoutResult(status -> handleIdempotently(handler, event));
                        } else {
                            handleIdempotently(handler, event);
                        }
                    } catch (Throwable ex) {
                        log.error("Error while handling event[{}:{}] by [{}]: ",
                                event.getType(), event.getEventId(), handler.getName(), ex);
                        errorHandlers.add(handler.getName());
                    }
                });

        if (isNotEmpty(errorHandlers)) {
            throw new RuntimeException("Error while consuming event[" + event.getType() + ":" + event.getEventId() + "] by the following handlers: " + errorHandlers);
        }
    }

    private void handleIdempotently(AbstractEventHandler<?> handler, ConsumingEvent consumingEvent) {
        if (handler.isIdempotent() || this.consumingEventDao.markEventAsConsumedByHandler(consumingEvent, handler)) {
            Operator operator = createPlatformOperator(EVENT_LISTENER, consumingEvent.getEvent().getClass().getSimpleName());
            ActorMdcSupport.of(operator).run(() -> ((AbstractEventHandler<Object>) handler).handle(consumingEvent.getEvent()));
        } else {
            log.warn("Event[{}:{}] has already been consumed by handler[{}], skip handling.",
                    consumingEvent.getEventId(), consumingEvent.getType(), handler.getName());
        }
    }
}
