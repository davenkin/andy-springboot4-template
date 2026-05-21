package com.company.andy.common.event.consume;

import com.company.andy.IntegrationTest;
import com.company.andy.common.model.actor.OrgActor;
import com.company.andy.feature.org.equipment.command.EquipmentCommandService;
import com.company.andy.feature.org.equipment.domain.EquipmentRepository;
import com.company.andy.feature.org.equipment.domain.event.EquipmentCreatedEvent;
import com.company.andy.feature.org.equipment.domain.event.EquipmentHolderUpdatedEvent;
import com.company.andy.feature.org.equipment.domain.event.EquipmentNameUpdatedEvent;
import com.company.andy.feature.org.maintenance.command.CreateMaintenanceRecordCommand;
import com.company.andy.feature.org.maintenance.command.MaintenanceRecordCommandService;
import com.company.andy.feature.org.maintenance.domain.event.MaintenanceRecordCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

import static com.company.andy.TestFixture.randomHumanUserOrgActor;
import static com.company.andy.common.event.DomainEventType.*;
import static com.company.andy.feature.org.equipment.EquipmentTestFixture.*;
import static com.company.andy.feature.org.maintenance.MaintenanceRecordTestFixture.randomCreateMaintenanceRecordCommand;
import static org.junit.jupiter.api.Assertions.*;

class EventConsumerIntegrationTest extends IntegrationTest {

    @Autowired
    private EquipmentCommandService equipmentCommandService;

    @Autowired
    private ConsumingEventDao consumingEventDao;

    @Autowired
    private TestingEquipmentCreatedEventHandler testingEquipmentCreatedEventHandler;

    @Autowired
    private TestingIdempotentEquipmentCreatedEventHandler testingIdempotentEquipmentCreatedEventHandler;

    @Autowired
    private TestingUrgentEquipmentCreatedEventHandler testingUrgentEquipmentCreatedEventHandler;

    @Autowired
    private TestingEquipmentUpdatedEventHandler testingEquipmentUpdatedEventHandler;

    @Autowired
    private TestingEquipmentNameUpdatedEventHandler testingEquipmentNameUpdatedEventHandler;

    @Autowired
    private TestingEquipmentStatusUpdatedEventHandler testingEquipmentStatusUpdatedEventHandler;

    @Autowired
    private TestingEquipmentHolderUpdatedEventHandler testingEquipmentHolderUpdatedEventHandler;

    @Autowired
    private TestingErrorNonTxEquipmentHolderUpdatedEventHandler testingErrorNonTxEquipmentHolderUpdatedEventHandler;

    @Autowired
    private TestingErrorTxEquipmentHolderUpdatedEventHandler testingErrorTxEquipmentHolderUpdatedEventHandler;

    @Autowired
    private MaintenanceRecordCommandService maintenanceRecordCommandService;

    @Autowired
    private EquipmentRepository equipmentRepository;


    @Test
    void handlers_should_only_handle_events_that_can_be_handled() {
        OrgActor actor = randomHumanUserOrgActor();
        String equipmentId = equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), actor);
        EquipmentCreatedEvent createdEvent = latestEventFor(equipmentId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);

        eventConsumer.consumeDomainEvent(createdEvent);

        assertEquals(1, testingEquipmentCreatedEventHandler.handledEvents.stream().filter(it -> it.event().getId().equals(createdEvent.getId())).count());
        assertEquals(0, testingEquipmentStatusUpdatedEventHandler.handledEvents.stream().filter(it -> it.event().getId().equals(createdEvent.getId())).count());
        assertTrue(consumingEventDao.exists(createdEvent.getId(), testingEquipmentCreatedEventHandler));
    }

    @Test
    void should_call_handlers_for_event_hierarchy() {
        OrgActor actor = randomHumanUserOrgActor();
        String equipmentId = equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), actor);
        equipmentCommandService.updateEquipmentName(equipmentId, randomUpdateEquipmentNameCommand(), actor);
        EquipmentNameUpdatedEvent updatedEvent = latestEventFor(equipmentId, EQUIPMENT_NAME_UPDATED_EVENT, EquipmentNameUpdatedEvent.class);

        eventConsumer.consumeDomainEvent(updatedEvent);

        assertEquals(1, testingEquipmentUpdatedEventHandler.handledEvents.stream().filter(it -> it.event().getId().equals(updatedEvent.getId())).count());
        assertEquals(1, testingEquipmentNameUpdatedEventHandler.handledEvents.stream().filter(it -> it.event().getId().equals(updatedEvent.getId())).count());
        assertTrue(consumingEventDao.exists(updatedEvent.getId(), testingEquipmentUpdatedEventHandler));
        assertTrue(consumingEventDao.exists(updatedEvent.getId(), testingEquipmentNameUpdatedEventHandler));
    }

    @Test
    void multiple_handlers_should_run_in_order_of_priority() {
        OrgActor actor = randomHumanUserOrgActor();
        String equipmentId = equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), actor);
        EquipmentCreatedEvent createdEvent = latestEventFor(equipmentId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);

        eventConsumer.consumeDomainEvent(createdEvent);

        Instant urgentHandledAt = testingUrgentEquipmentCreatedEventHandler.handledEvents.stream().filter(it -> it.event().getId().equals(createdEvent.getId())).findFirst().get().handledAt();
        Instant secondHandledAt = testingEquipmentCreatedEventHandler.handledEvents.stream().filter(it -> it.event().getId().equals(createdEvent.getId())).findFirst().get().handledAt();
        Instant thirdHandledAt = testingIdempotentEquipmentCreatedEventHandler.handledEvents.stream().filter(it -> it.event().getId().equals(createdEvent.getId())).findFirst().get().handledAt();
        assertTrue(urgentHandledAt.isBefore(secondHandledAt));
        assertTrue(secondHandledAt.isBefore(thirdHandledAt));
    }

    @Test
    void should_record_consumed_or_not_if_handler_throws_exception() {
        OrgActor actor = randomHumanUserOrgActor();
        String equipmentId = equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), actor);
        equipmentCommandService.updateEquipmentHolder(equipmentId, randomUpdateEquipmentHolderCommand(), actor);
        EquipmentHolderUpdatedEvent holderUpdatedEvent = latestEventFor(equipmentId, EQUIPMENT_HOLDER_UPDATED_EVENT, EquipmentHolderUpdatedEvent.class);

        assertThrows(RuntimeException.class, () -> eventConsumer.consumeDomainEvent(holderUpdatedEvent));

        assertTrue(consumingEventDao.exists(holderUpdatedEvent.getId(), testingErrorNonTxEquipmentHolderUpdatedEventHandler));
        assertFalse(consumingEventDao.exists(holderUpdatedEvent.getId(), testingErrorTxEquipmentHolderUpdatedEventHandler));
    }

    @Test
    void should_not_mark_as_consumed_for_idempotent_handler() {
        OrgActor actor = randomHumanUserOrgActor();
        String equipmentId = equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), actor);
        EquipmentCreatedEvent createdEvent = latestEventFor(equipmentId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);

        eventConsumer.consumeDomainEvent(createdEvent);

        assertTrue(consumingEventDao.exists(createdEvent.getId(), testingEquipmentCreatedEventHandler));
        assertFalse(consumingEventDao.exists(createdEvent.getId(), testingIdempotentEquipmentCreatedEventHandler));
    }

    @Test
    void multiple_handlers_should_run_independently() {
        OrgActor actor = randomHumanUserOrgActor();
        String equipmentId = equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), actor);
        equipmentCommandService.updateEquipmentHolder(equipmentId, randomUpdateEquipmentHolderCommand(), actor);
        EquipmentHolderUpdatedEvent holderUpdatedEvent = latestEventFor(equipmentId, EQUIPMENT_HOLDER_UPDATED_EVENT, EquipmentHolderUpdatedEvent.class);

        assertThrows(RuntimeException.class, () -> eventConsumer.consumeDomainEvent(holderUpdatedEvent));

        assertEquals(1, testingEquipmentHolderUpdatedEventHandler.handledEvents.stream().filter(it -> it.event().getId().equals(holderUpdatedEvent.getId())).count());
        assertEquals(1, testingErrorNonTxEquipmentHolderUpdatedEventHandler.handledEvents.stream().filter(it -> it.event().getId().equals(holderUpdatedEvent.getId())).count());
        assertEquals(1, testingErrorTxEquipmentHolderUpdatedEventHandler.handledEvents.stream().filter(it -> it.event().getId().equals(holderUpdatedEvent.getId())).count());
    }

    @Test
    void should_run_for_duplicated_event_if_idempotent_handler() {
        OrgActor actor = randomHumanUserOrgActor();
        String equipmentId = equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), actor);
        EquipmentCreatedEvent createdEvent = latestEventFor(equipmentId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);

        eventConsumer.consumeDomainEvent(createdEvent);
        assertEquals(1, testingIdempotentEquipmentCreatedEventHandler.handledEvents.stream().filter(it -> it.event().getId().equals(createdEvent.getId())).count());
        assertEquals(1, testingUrgentEquipmentCreatedEventHandler.handledEvents.stream().filter(it -> it.event().getId().equals(createdEvent.getId())).count());

        // consume again
        eventConsumer.consumeDomainEvent(createdEvent);
        assertEquals(2, testingIdempotentEquipmentCreatedEventHandler.handledEvents.stream().filter(it -> it.event().getId().equals(createdEvent.getId())).count());
        assertEquals(1, testingUrgentEquipmentCreatedEventHandler.handledEvents.stream().filter(it -> it.event().getId().equals(createdEvent.getId())).count());
    }

    @Test
    void event_handler_can_further_raise_events_and_been_handled() {
        OrgActor actor = randomHumanUserOrgActor();
        String equipmentId = equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), actor);
        assertNull(equipmentRepository.byId(equipmentId).getStatus());

        CreateMaintenanceRecordCommand createMaintenanceRecordCommand = randomCreateMaintenanceRecordCommand(equipmentId);
        String maintenanceRecordId = maintenanceRecordCommandService.createMaintenanceRecord(createMaintenanceRecordCommand, actor);
        MaintenanceRecordCreatedEvent maintenanceRecordCreatedEvent = latestEventFor(maintenanceRecordId, MAINTENANCE_RECORD_CREATED_EVENT, MaintenanceRecordCreatedEvent.class);
        eventConsumer.consumeDomainEvent(maintenanceRecordCreatedEvent);

        assertEquals(createMaintenanceRecordCommand.status(), equipmentRepository.byId(equipmentId).getStatus());
    }
}