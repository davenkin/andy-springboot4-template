package com.company.andy.common.infrastructure;

import com.company.andy.IntegrationTest;
import com.company.andy.common.exception.ServiceException;
import com.company.andy.common.model.operator.Operator;
import com.company.andy.sample.equipment.domain.Equipment;
import com.company.andy.sample.equipment.domain.EquipmentFactory;
import com.company.andy.sample.equipment.domain.EquipmentRepository;
import com.company.andy.sample.equipment.domain.event.EquipmentCreatedEvent;
import com.company.andy.sample.equipment.domain.event.EquipmentDeletedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.company.andy.RandomTestUtils.randomEquipmentName;
import static com.company.andy.RandomTestUtils.randomUserOperator;
import static com.company.andy.common.event.DomainEventType.EQUIPMENT_CREATED_EVENT;
import static com.company.andy.common.event.DomainEventType.EQUIPMENT_DELETED_EVENT;
import static com.company.andy.common.exception.ErrorCode.AR_NOT_FOUND;
import static com.company.andy.common.exception.ErrorCode.NOT_SAME_ORG;
import static org.apache.commons.lang3.RandomStringUtils.secure;
import static org.junit.jupiter.api.Assertions.*;

class AbstractMongoRepositoryIntegrationTest extends IntegrationTest {

    @Autowired
    private EquipmentFactory equipmentFactory;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Test
    void should_save_ar() {
        Operator operator = randomUserOperator();
        Equipment equipment = equipmentFactory.create(randomEquipmentName(), operator);
        assertEquals(1, equipment.getEvents().size());
        assertInstanceOf(EquipmentCreatedEvent.class, equipment.getEvents().get(0));
        equipmentRepository.save(equipment);

        assertNull(equipment.getEvents());

        EquipmentCreatedEvent createdEvent = latestEventFor(equipment.getId(), EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        assertEquals(equipment.getId(), createdEvent.getEquipmentId());
        Equipment dbAr = equipmentRepository.byIdOptional(equipment.getId()).get();
        assertNull(dbAr.getEvents());
    }

    @Test
    void should_save_ars() {
        Operator operator = randomUserOperator();
        Equipment equipment1 = equipmentFactory.create(randomEquipmentName(), operator);
        Equipment equipment2 = equipmentFactory.create(randomEquipmentName(), operator);

        equipmentRepository.save(List.of(equipment1, equipment2));

        assertTrue(equipmentRepository.byIdOptional(equipment1.getId()).isPresent());
        assertTrue(equipmentRepository.byIdOptional(equipment2.getId()).isPresent());
        EquipmentCreatedEvent createdEvent1 = latestEventFor(equipment1.getId(), EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        assertEquals(equipment1.getId(), createdEvent1.getArId());
        EquipmentCreatedEvent createdEvent2 = latestEventFor(equipment2.getId(), EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        assertEquals(equipment2.getId(), createdEvent2.getEquipmentId());
    }

    @Test
    void should_throw_exception_if_not_the_same_org() {
        Equipment equipment1 = equipmentFactory.create(randomEquipmentName(), randomUserOperator());
        Equipment equipment2 = equipmentFactory.create(randomEquipmentName(), randomUserOperator());

        ServiceException exception = assertThrows(ServiceException.class, () -> equipmentRepository.save(List.of(equipment1, equipment2)));
        assertEquals(NOT_SAME_ORG, exception.getCode());
    }

    @Test
    void should_delete_ar() {
        Operator operator = randomUserOperator();
        Equipment equipment = equipmentFactory.create(randomEquipmentName(), operator);

        equipmentRepository.save(equipment);
        assertTrue(equipmentRepository.byIdOptional(equipment.getId()).isPresent());

        equipmentRepository.delete(equipment);
        assertNull(equipment.getEvents());
        assertFalse(equipmentRepository.byIdOptional(equipment.getId()).isPresent());
        EquipmentDeletedEvent deletedEvent = latestEventFor(equipment.getId(), EQUIPMENT_DELETED_EVENT, EquipmentDeletedEvent.class);
        assertEquals(equipment.getId(), deletedEvent.getEquipmentId());
    }

    @Test
    void should_delete_ars() {
        Operator operator = randomUserOperator();
        Equipment equipment1 = equipmentFactory.create(randomEquipmentName(), operator);
        Equipment equipment2 = equipmentFactory.create(randomEquipmentName(), operator);

        equipmentRepository.save(List.of(equipment1, equipment2));

        assertTrue(equipmentRepository.byIdOptional(equipment1.getId()).isPresent());
        assertTrue(equipmentRepository.byIdOptional(equipment2.getId()).isPresent());

        equipmentRepository.delete(List.of(equipment1, equipment2));
        assertNull(equipment1.getEvents());
        assertNull(equipment2.getEvents());
        assertFalse(equipmentRepository.byIdOptional(equipment1.getId()).isPresent());
        assertFalse(equipmentRepository.byIdOptional(equipment2.getId()).isPresent());

        EquipmentDeletedEvent deletedEvent1 = latestEventFor(equipment1.getId(), EQUIPMENT_DELETED_EVENT, EquipmentDeletedEvent.class);
        assertEquals(equipment1.getId(), deletedEvent1.getEquipmentId());
        EquipmentDeletedEvent deletedEvent2 = latestEventFor(equipment2.getId(), EQUIPMENT_DELETED_EVENT, EquipmentDeletedEvent.class);
        assertEquals(equipment2.getId(), deletedEvent2.getEquipmentId());
    }

    @Test
    void should_throw_exception_if_not_the_same_org_for_delete() {
        Equipment equipment1 = equipmentFactory.create(randomEquipmentName(), randomUserOperator());
        Equipment equipment2 = equipmentFactory.create(randomEquipmentName(), randomUserOperator());

        ServiceException exception = assertThrows(ServiceException.class, () -> equipmentRepository.delete(List.of(equipment1, equipment2)));
        assertEquals(NOT_SAME_ORG, exception.getCode());
    }

    @Test
    void should_fetch_ar_by_id() {
        Operator operator = randomUserOperator();
        Equipment equipment = equipmentFactory.create(randomEquipmentName(), operator);
        assertFalse(equipmentRepository.exists(equipment.getId(), operator.getOrgId()));

        equipmentRepository.save(equipment);

        assertEquals(equipment.getId(), equipmentRepository.byId(equipment.getId()).getId());
        assertEquals(equipment.getId(), equipmentRepository.byId(equipment.getId(), operator.getOrgId()).getId());
        assertEquals(equipment.getId(), equipmentRepository.byIdOptional(equipment.getId(), operator.getOrgId()).get().getId());
        assertEquals(equipment.getId(), equipmentRepository.byIdOptional(equipment.getId()).get().getId());
        assertTrue(equipmentRepository.exists(equipment.getId(), operator.getOrgId()));

        assertEquals(AR_NOT_FOUND, assertThrows(ServiceException.class, () -> equipmentRepository.byId(secure().nextAlphanumeric(5), secure().nextAlphanumeric(5))).getCode());
        assertEquals(AR_NOT_FOUND, assertThrows(ServiceException.class, () -> equipmentRepository.byId(secure().nextAlphanumeric(5))).getCode());

        assertFalse(equipmentRepository.byIdOptional(secure().nextAlphanumeric(5)).isPresent());
        assertFalse(equipmentRepository.byIdOptional(secure().nextAlphanumeric(5), secure().nextAlphanumeric(5)).isPresent());
    }

}