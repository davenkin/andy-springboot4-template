package com.company.andy.common.infrastructure.transaction;

import com.company.andy.IntegrationTest;
import com.company.andy.common.exception.ServiceException;
import com.company.andy.common.model.operator.Operator;
import com.company.andy.feature.equipment.command.CreateEquipmentCommand;
import com.company.andy.feature.equipment.command.EquipmentCommandService;
import com.company.andy.feature.equipment.command.UpdateEquipmentHolderCommand;
import com.company.andy.feature.equipment.command.UpdateEquipmentNameCommand;
import com.company.andy.feature.equipment.domain.Equipment;
import com.company.andy.feature.equipment.domain.EquipmentRepository;
import com.company.andy.feature.equipment.domain.event.EquipmentNameUpdatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import static com.company.andy.RandomTestUtils.*;
import static com.company.andy.common.event.DomainEventType.EQUIPMENT_NAME_UPDATED_EVENT;
import static org.junit.jupiter.api.Assertions.*;

@DisabledIf(value = "#{environment.acceptsProfiles('it')}", loadContext = true)
class TransactionIntegrationTest extends IntegrationTest {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private EquipmentCommandService equipmentCommandService;

    @Autowired
    protected TransactionTestingService transactionTestingService;

    @Test
    void transaction_should_work_for_aggregate_root_and_domain_event() {
        Operator operator = randomUserOperator();
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();
        CreateEquipmentCommand createAnotherEquipmentCommand = randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, operator);
        equipmentCommandService.createEquipment(createAnotherEquipmentCommand, operator);

        UpdateEquipmentNameCommand updateEquipmentNameCommand = new UpdateEquipmentNameCommand(createAnotherEquipmentCommand.name());
        assertThrows(ServiceException.class,
                () -> equipmentCommandService.updateEquipmentName(equipmentId, updateEquipmentNameCommand, operator));

        assertEquals(createEquipmentCommand.name(), equipmentRepository.byId(equipmentId).getName());
        assertNull(latestEventFor(equipmentId, EQUIPMENT_NAME_UPDATED_EVENT, EquipmentNameUpdatedEvent.class));
    }

    @Test
    void should_not_work_for_multiple_aggregate_roots_when_exception_thrown_with_transaction() {
        Operator operator = randomUserOperator();
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, operator);
        UpdateEquipmentNameCommand updateEquipmentNameCommand = randomUpdateEquipmentNameCommand();
        UpdateEquipmentHolderCommand updateEquipmentHolderCommand = randomUpdateEquipmentHolderCommand();

        assertThrows(RuntimeException.class, () -> transactionTestingService.throwExceptionWithTransaction(equipmentId,
                updateEquipmentNameCommand,
                updateEquipmentHolderCommand, operator));

        Equipment dbEquipment = equipmentRepository.byId(equipmentId);
        assertNotEquals(updateEquipmentNameCommand.name(), dbEquipment.getName());
        assertNotEquals(updateEquipmentHolderCommand.name(), dbEquipment.getHolder());
        assertNull(latestEventFor(equipmentId, EQUIPMENT_NAME_UPDATED_EVENT, EquipmentNameUpdatedEvent.class));
    }

    @Test
    void should_work_for_multiple_aggregate_roots_when_exception_thrown_at_the_end_without_transaction() {
        Operator operator = randomUserOperator();
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, operator);
        UpdateEquipmentNameCommand updateEquipmentNameCommand = randomUpdateEquipmentNameCommand();
        UpdateEquipmentHolderCommand updateEquipmentHolderCommand = randomUpdateEquipmentHolderCommand();

        assertThrows(RuntimeException.class, () -> transactionTestingService.throwExceptionAtTheEndWithoutTransaction(equipmentId,
                updateEquipmentNameCommand,
                updateEquipmentHolderCommand, operator));

        Equipment dbEquipment = equipmentRepository.byId(equipmentId);
        assertEquals(updateEquipmentNameCommand.name(), dbEquipment.getName());
        assertEquals(updateEquipmentHolderCommand.name(), dbEquipment.getHolder());
        assertNotNull(latestEventFor(equipmentId, EQUIPMENT_NAME_UPDATED_EVENT, EquipmentNameUpdatedEvent.class));
    }

    @Test
    void should_not_work_for_multiple_aggregate_roots_when_exception_thrown_in_the_middle_without_transaction() {
        Operator operator = randomUserOperator();
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, operator);
        UpdateEquipmentNameCommand updateEquipmentNameCommand = randomUpdateEquipmentNameCommand();
        UpdateEquipmentHolderCommand updateEquipmentHolderCommand = randomUpdateEquipmentHolderCommand();

        assertThrows(RuntimeException.class, () -> transactionTestingService.throwExceptionInTheMiddleWithoutTransaction(equipmentId,
                updateEquipmentNameCommand,
                updateEquipmentHolderCommand, operator));

        Equipment dbEquipment = equipmentRepository.byId(equipmentId);
        assertEquals(updateEquipmentNameCommand.name(), dbEquipment.getName());
        assertNotEquals(updateEquipmentHolderCommand.name(), dbEquipment.getHolder());
        assertNotNull(latestEventFor(equipmentId, EQUIPMENT_NAME_UPDATED_EVENT, EquipmentNameUpdatedEvent.class));
    }
}