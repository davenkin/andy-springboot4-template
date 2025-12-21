package com.company.andy.common.event.publish;

import com.company.andy.IntegrationTest;
import com.company.andy.common.event.publish.infrastructure.FakeDomainEventSender;
import com.company.andy.common.model.operator.Operator;
import com.company.andy.sample.equipment.command.CreateEquipmentCommand;
import com.company.andy.sample.equipment.command.EquipmentCommandService;
import com.company.andy.sample.equipment.domain.event.EquipmentCreatedEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.concurrent.CompletableFuture;

import static com.company.andy.RandomTestUtils.randomEquipmentName;
import static com.company.andy.RandomTestUtils.randomUserOperator;
import static com.company.andy.common.event.DomainEventType.EQUIPMENT_CREATED_EVENT;
import static com.company.andy.common.event.publish.DomainEventPublishStatus.*;
import static java.util.concurrent.CompletableFuture.failedFuture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doReturn;

class DomainEventPublishJobIntegrationTest extends IntegrationTest {

    @Autowired
    private DomainEventPublishJob domainEventPublishJob;

    @Autowired
    private EquipmentCommandService equipmentCommandService;

    @Autowired
    private PublishingDomainEventDao publishingDomainEventDao;

    @MockitoSpyBean
    private FakeDomainEventSender domainEventSender;

    @Test
    void should_publish_domain_events() {
        Operator operator = randomUserOperator();
        String arId1 = equipmentCommandService.createEquipment(CreateEquipmentCommand.builder().name(randomEquipmentName()).build(), operator);
        String arId2 = equipmentCommandService.createEquipment(CreateEquipmentCommand.builder().name(randomEquipmentName()).build(), operator);
        String arId3 = equipmentCommandService.createEquipment(CreateEquipmentCommand.builder().name(randomEquipmentName()).build(), operator);
        String arId4 = equipmentCommandService.createEquipment(CreateEquipmentCommand.builder().name(randomEquipmentName()).build(), operator);

        EquipmentCreatedEvent event1 = latestEventFor(arId1, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        EquipmentCreatedEvent event2 = latestEventFor(arId2, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        EquipmentCreatedEvent event3 = latestEventFor(arId3, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        EquipmentCreatedEvent event4 = latestEventFor(arId4, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        assertEquals(CREATED, publishingDomainEventDao.byId(event1.getId()).getStatus());
        assertEquals(CREATED, publishingDomainEventDao.byId(event2.getId()).getStatus());
        assertEquals(CREATED, publishingDomainEventDao.byId(event3.getId()).getStatus());
        assertEquals(CREATED, publishingDomainEventDao.byId(event4.getId()).getStatus());

        domainEventPublishJob.publishStagedDomainEvents(500);

        assertEquals(PUBLISH_SUCCEED, publishingDomainEventDao.byId(event1.getId()).getStatus());
        assertEquals(PUBLISH_SUCCEED, publishingDomainEventDao.byId(event2.getId()).getStatus());
        assertEquals(PUBLISH_SUCCEED, publishingDomainEventDao.byId(event3.getId()).getStatus());
        assertEquals(PUBLISH_SUCCEED, publishingDomainEventDao.byId(event4.getId()).getStatus());

        assertTrue(domainEventSender.getEvents().containsKey(event1.getId()));
        assertTrue(domainEventSender.getEvents().containsKey(event2.getId()));
        assertTrue(domainEventSender.getEvents().containsKey(event3.getId()));
        assertTrue(domainEventSender.getEvents().containsKey(event4.getId()));
    }

    @Test
    void should_fail_publish_domain_events_with_max_of_3_attempts() {
        Operator operator = randomUserOperator();
        String arId = equipmentCommandService.createEquipment(CreateEquipmentCommand.builder().name(randomEquipmentName()).build(), operator);
        EquipmentCreatedEvent event = latestEventFor(arId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        doReturn(failedFuture(new RuntimeException("stub exception")))
                .when(domainEventSender).send(argThat(it -> it.getId().equals(event.getId())));
        domainEventPublishJob.publishStagedDomainEvents(500);
        PublishingDomainEvent publishingDomainEvent1 = publishingDomainEventDao.byId(event.getId());
        assertEquals(PUBLISH_FAILED, publishingDomainEvent1.getStatus());
        assertEquals(1, publishingDomainEvent1.getPublishedCount());

        domainEventPublishJob.publishStagedDomainEvents(500);
        PublishingDomainEvent publishingDomainEvent2 = publishingDomainEventDao.byId(event.getId());
        assertEquals(PUBLISH_FAILED, publishingDomainEvent2.getStatus());
        assertEquals(2, publishingDomainEvent2.getPublishedCount());

        domainEventPublishJob.publishStagedDomainEvents(500);
        PublishingDomainEvent publishingDomainEvent3 = publishingDomainEventDao.byId(event.getId());
        assertEquals(PUBLISH_FAILED, publishingDomainEvent3.getStatus());
        assertEquals(3, publishingDomainEvent3.getPublishedCount());

        domainEventPublishJob.publishStagedDomainEvents(500);
        PublishingDomainEvent publishingDomainEvent4 = publishingDomainEventDao.byId(event.getId());
        assertEquals(PUBLISH_FAILED, publishingDomainEvent4.getStatus());
        assertEquals(3, publishingDomainEvent4.getPublishedCount());
    }

    @Test
    void should_publish_successfully_if_sender_recovered() {
        Operator operator = randomUserOperator();
        String arId = equipmentCommandService.createEquipment(CreateEquipmentCommand.builder().name(randomEquipmentName()).build(), operator);
        EquipmentCreatedEvent event = latestEventFor(arId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        doReturn(failedFuture(new RuntimeException("stub exception")))
                .doReturn(CompletableFuture.completedFuture(event.getId()))
                .when(domainEventSender).send(ArgumentMatchers.argThat(it -> it.getId().equals(event.getId())));

        domainEventPublishJob.publishStagedDomainEvents(500);
        PublishingDomainEvent publishingDomainEvent1 = publishingDomainEventDao.byId(event.getId());
        assertEquals(PUBLISH_FAILED, publishingDomainEvent1.getStatus());
        assertEquals(1, publishingDomainEvent1.getPublishedCount());

        domainEventPublishJob.publishStagedDomainEvents(500);
        PublishingDomainEvent publishingDomainEvent2 = publishingDomainEventDao.byId(event.getId());
        assertEquals(PUBLISH_SUCCEED, publishingDomainEvent2.getStatus());
        assertEquals(2, publishingDomainEvent2.getPublishedCount());
    }

}