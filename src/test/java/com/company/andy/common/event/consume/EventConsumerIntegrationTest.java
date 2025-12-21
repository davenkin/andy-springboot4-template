package com.company.andy.common.event.consume;

import com.company.andy.IntegrationTest;
import com.company.andy.common.model.operator.Operator;
import com.company.andy.sample.equipment.command.CreateEquipmentCommand;
import com.company.andy.sample.equipment.command.EquipmentCommandService;
import com.company.andy.sample.equipment.command.UpdateEquipmentNameCommand;
import com.company.andy.sample.equipment.domain.event.EquipmentCreatedEvent;
import com.company.andy.sample.equipment.domain.event.EquipmentNameUpdatedEvent;
import com.company.andy.sample.equipment.eventhandler.EquipmentCreatedEventHandler;
import com.company.andy.sample.equipment.eventhandler.EquipmentCreatedEventHandler2;
import com.company.andy.sample.equipment.eventhandler.EquipmentNameUpdatedEventHandler;
import com.company.andy.sample.equipment.eventhandler.EquipmentUpdatedEventHandler;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static com.company.andy.RandomTestUtils.randomEquipmentName;
import static com.company.andy.RandomTestUtils.randomUserOperator;
import static com.company.andy.common.event.DomainEventType.EQUIPMENT_CREATED_EVENT;
import static com.company.andy.common.event.DomainEventType.EQUIPMENT_NAME_UPDATED_EVENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventConsumerIntegrationTest extends IntegrationTest {

    @Autowired
    private EquipmentCommandService equipmentCommandService;

    @MockitoSpyBean
    private EquipmentCreatedEventHandler createdEventHandler;

    @MockitoSpyBean
    private EquipmentCreatedEventHandler2 createdEventHandler2;

    @MockitoSpyBean
    private EquipmentUpdatedEventHandler updatedEventHandler;

    @MockitoSpyBean
    private EquipmentNameUpdatedEventHandler nameUpdatedEventHandler;

    @MockitoSpyBean
    private ConsumingEventDao consumingEventDao;

    @Test
    void should_only_handle_events_that_can_be_handled() {
        Operator operator = randomUserOperator();
        String arId = equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), operator);
        EquipmentCreatedEvent createdEvent = latestEventFor(arId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);

        eventConsumer.consumeDomainEvent(createdEvent);

        verify(createdEventHandler, times(1)).handle(any(EquipmentCreatedEvent.class));
        verify(updatedEventHandler, times(0)).handle(any());
        verify(consumingEventDao).markEventAsConsumedByHandler(any(ConsumingEvent.class), any(EquipmentCreatedEventHandler.class));
        assertTrue(consumingEventDao.exists(createdEvent.getId()));
    }

    @Test
    void should_call_handler_for_event_hierarchy() {
        Operator operator = randomUserOperator();
        String arId = equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), operator);
        equipmentCommandService.updateEquipmentName(arId, randomUpdateEquipmentNameCommand(), operator);
        EquipmentNameUpdatedEvent updatedEvent = latestEventFor(arId, EQUIPMENT_NAME_UPDATED_EVENT, EquipmentNameUpdatedEvent.class);

        eventConsumer.consumeDomainEvent(updatedEvent);

        verify(nameUpdatedEventHandler).handle(any(EquipmentNameUpdatedEvent.class));
        verify(updatedEventHandler).handle(any(EquipmentNameUpdatedEvent.class));
        assertTrue(consumingEventDao.exists(updatedEvent.getId(), nameUpdatedEventHandler));
        assertTrue(consumingEventDao.exists(updatedEvent.getId(), updatedEventHandler));
    }

    @Test
    void multiple_handlers_should_run_in_order_of_priority() {
        Operator operator = randomUserOperator();
        String arId = equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), operator);
        EquipmentCreatedEvent createdEvent = latestEventFor(arId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        when(createdEventHandler.priority()).thenReturn(0);
        when(createdEventHandler2.priority()).thenReturn(1);

        eventConsumer.consumeDomainEvent(createdEvent);

        InOrder inOrder = Mockito.inOrder(createdEventHandler, createdEventHandler2);
        inOrder.verify(createdEventHandler).handle(any(EquipmentCreatedEvent.class));
        inOrder.verify(createdEventHandler2).handle(any(EquipmentCreatedEvent.class));
        assertTrue(consumingEventDao.exists(createdEvent.getId(), createdEventHandler));
        assertTrue(consumingEventDao.exists(createdEvent.getId(), createdEventHandler2));
    }

    @Test
    void multiple_handlers_should_run_in_order_of_priority_reversely() {
        Operator operator = randomUserOperator();
        String arId = equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), operator);
        EquipmentCreatedEvent createdEvent = latestEventFor(arId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        when(createdEventHandler.priority()).thenReturn(1);
        when(createdEventHandler2.priority()).thenReturn(0);

        eventConsumer.consumeDomainEvent(createdEvent);

        InOrder inOrder = Mockito.inOrder(createdEventHandler, createdEventHandler2);
        inOrder.verify(createdEventHandler2).handle(any(EquipmentCreatedEvent.class));
        inOrder.verify(createdEventHandler).handle(any(EquipmentCreatedEvent.class));
        assertTrue(consumingEventDao.exists(createdEvent.getId(), createdEventHandler));
        assertTrue(consumingEventDao.exists(createdEvent.getId(), createdEventHandler2));
    }

    @Test
    void should_mark_as_consumed_if_non_transactional_handler_throws_exception() {
        Operator operator = randomUserOperator();
        String arId = equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), operator);
        EquipmentCreatedEvent createdEvent = latestEventFor(arId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        doThrow(new RuntimeException("stub exception")).when(createdEventHandler).handle(any(EquipmentCreatedEvent.class));
        when(createdEventHandler.isTransactional()).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> eventConsumer.consumeDomainEvent(createdEvent));

        assertTrue(exception.getMessage().startsWith("Error while consuming event"));
        verify(consumingEventDao).markEventAsConsumedByHandler(any(ConsumingEvent.class), any(EquipmentCreatedEventHandler.class));
        assertTrue(consumingEventDao.exists(createdEvent.getId()));
    }

    @Test
    void should_not_mark_as_consumed_if_transactional_handler_throws_exception() {
        Operator operator = randomUserOperator();
        String arId = equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), operator);
        EquipmentCreatedEvent createdEvent = latestEventFor(arId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        doThrow(new RuntimeException("stub exception")).when(createdEventHandler).handle(any(EquipmentCreatedEvent.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> eventConsumer.consumeDomainEvent(createdEvent));

        assertTrue(exception.getMessage().startsWith("Error while consuming event"));
        verify(consumingEventDao).markEventAsConsumedByHandler(any(ConsumingEvent.class), any(EquipmentCreatedEventHandler.class));
        assertFalse(consumingEventDao.exists(createdEvent.getId(), createdEventHandler));
    }

    @Test
    void should_not_mark_as_consumed_for_idempotent_handler() {
        Operator operator = randomUserOperator();
        String arId = equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), operator);
        EquipmentCreatedEvent createdEvent = latestEventFor(arId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        when(createdEventHandler.isIdempotent()).thenReturn(true);

        eventConsumer.consumeDomainEvent(createdEvent);

        verify(consumingEventDao, times(0)).markEventAsConsumedByHandler(any(), any(EquipmentCreatedEventHandler.class));
        verify(consumingEventDao, times(1)).markEventAsConsumedByHandler(any(), any(EquipmentCreatedEventHandler2.class));
        assertFalse(consumingEventDao.exists(createdEvent.getId(), createdEventHandler));
        verify(createdEventHandler, times(1)).handle(any(EquipmentCreatedEvent.class));
    }

    @Test
    void multiple_handlers_should_run_independently() {
        Operator operator = randomUserOperator();
        String arId = equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), operator);
        equipmentCommandService.updateEquipmentName(arId, randomUpdateEquipmentNameCommand(), operator);
        EquipmentNameUpdatedEvent updatedEvent = latestEventFor(arId, EQUIPMENT_NAME_UPDATED_EVENT, EquipmentNameUpdatedEvent.class);
        when(nameUpdatedEventHandler.priority()).thenReturn(0);
        when(updatedEventHandler.priority()).thenReturn(1);
        doThrow(new RuntimeException("stub exception")).when(nameUpdatedEventHandler).handle(any(EquipmentNameUpdatedEvent.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> eventConsumer.consumeDomainEvent(updatedEvent));

        assertTrue(exception.getMessage().startsWith("Error while consuming event"));
        verify(nameUpdatedEventHandler).handle(any(EquipmentNameUpdatedEvent.class));
        verify(updatedEventHandler).handle(any(EquipmentNameUpdatedEvent.class));
    }

    @Test
    void should_run_again_for_idempotent_handler() {
        Operator operator = randomUserOperator();
        String arId = equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), operator);
        EquipmentCreatedEvent createdEvent = latestEventFor(arId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        when(createdEventHandler.isIdempotent()).thenReturn(true);

        eventConsumer.consumeDomainEvent(createdEvent);
        eventConsumer.consumeDomainEvent(createdEvent);

        verify(createdEventHandler, times(2)).handle(any(EquipmentCreatedEvent.class));
    }

    @Test
    void should_not_handle_again_for_non_idempotent_handler() {
        Operator operator = randomUserOperator();
        String arId = equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), operator);
        EquipmentCreatedEvent createdEvent = latestEventFor(arId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        when(createdEventHandler.isIdempotent()).thenReturn(false);

        eventConsumer.consumeDomainEvent(createdEvent);
        eventConsumer.consumeDomainEvent(createdEvent);

        verify(consumingEventDao, times(2)).markEventAsConsumedByHandler(any(), any(EquipmentCreatedEventHandler.class));
        verify(createdEventHandler, times(1)).handle(any(EquipmentCreatedEvent.class));
    }


    private static UpdateEquipmentNameCommand randomUpdateEquipmentNameCommand() {
        return new UpdateEquipmentNameCommand(randomEquipmentName());
    }

    private static CreateEquipmentCommand randomCreateEquipmentCommand() {
        return new CreateEquipmentCommand(randomEquipmentName());
    }

}