package com.company.andy.common.event;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;

import static com.company.andy.TestFixture.randomHumanUserOrgActor;
import static com.company.andy.common.model.OrgRole.ORG_ADMIN;
import static com.company.andy.feature.equipment.EquipmentTestFixture.randomEquipmentName;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import com.company.andy.IntegrationTest;
import com.company.andy.common.event.consume.ConsumingEvent;
import com.company.andy.common.event.consume.ConsumingEventDao;
import com.company.andy.common.event.publish.PublishingDomainEventDao;
import com.company.andy.common.model.actor.OrgActor;
import com.company.andy.feature.equipment.domain.Equipment;
import com.company.andy.feature.equipment.domain.event.EquipmentCreatedEvent;
import com.company.andy.feature.equipment.eventhandler.EquipmentCreatedEventHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

class DomainEventHouseKeepingJobIntegrationTest extends IntegrationTest {
  @Autowired
  private ConsumingEventDao consumingEventDao;

  @Autowired
  private PublishingDomainEventDao publishingDomainEventDao;

  @Autowired
  private DomainEventHouseKeepingJob domainEventHouseKeepingJob;

  @Autowired
  private EquipmentCreatedEventHandler equipmentCreatedEventHandler;

  @Test
  void should_remove_old_publishing_domain_events_from_mongo() {
    OrgActor actor = randomHumanUserOrgActor(ORG_ADMIN);
    EquipmentCreatedEvent event1 = new EquipmentCreatedEvent(new Equipment(randomEquipmentName(), actor), actor);
    EquipmentCreatedEvent event2 = new EquipmentCreatedEvent(new Equipment(randomEquipmentName(), actor), actor);
    ReflectionTestUtils.setField(event1, DomainEvent.Fields.raisedAt, now().minus(110, DAYS));
    ReflectionTestUtils.setField(event2, DomainEvent.Fields.raisedAt, now().minus(90, DAYS));
    publishingDomainEventDao.stage(List.of(event1, event2));
    assertNotNull(publishingDomainEventDao.byId(event1.getId()));
    assertNotNull(publishingDomainEventDao.byId(event2.getId()));

    domainEventHouseKeepingJob.removeOldPublishingDomainEventsFromMongo(100);

    assertNull(publishingDomainEventDao.byId(event1.getId()));
    assertNotNull(publishingDomainEventDao.byId(event2.getId()));
  }

  @Test
  void should_remove_old_consuming_domain_events_from_mongo() {
    OrgActor actor = randomHumanUserOrgActor(ORG_ADMIN);
    EquipmentCreatedEvent event1 = new EquipmentCreatedEvent(new Equipment(randomEquipmentName(), actor), actor);
    EquipmentCreatedEvent event2 = new EquipmentCreatedEvent(new Equipment(randomEquipmentName(), actor), actor);
    ConsumingEvent consumingEvent1 = new ConsumingEvent(event1.getId(), event1);
    ConsumingEvent consumingEvent2 = new ConsumingEvent(event2.getId(), event1);
    ReflectionTestUtils.setField(consumingEvent1, ConsumingEvent.Fields.consumedAt, now().minus(110, DAYS));
    ReflectionTestUtils.setField(consumingEvent2, ConsumingEvent.Fields.consumedAt, now().minus(90, DAYS));
    consumingEventDao.markEventAsConsumedByHandler(consumingEvent1, equipmentCreatedEventHandler);
    consumingEventDao.markEventAsConsumedByHandler(consumingEvent2, equipmentCreatedEventHandler);
    assertTrue(consumingEventDao.exists(event1.getId(), equipmentCreatedEventHandler));
    assertTrue(consumingEventDao.exists(event2.getId(), equipmentCreatedEventHandler));

    domainEventHouseKeepingJob.removeOldConsumingDomainEventsFromMongo(100);

    assertFalse(consumingEventDao.exists(event1.getId(), equipmentCreatedEventHandler));
    assertTrue(consumingEventDao.exists(event2.getId(), equipmentCreatedEventHandler));
  }
}