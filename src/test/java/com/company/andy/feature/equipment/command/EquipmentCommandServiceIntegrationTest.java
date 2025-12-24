package com.company.andy.feature.equipment.command;

import com.company.andy.IntegrationTest;
import com.company.andy.common.model.operator.Operator;
import com.company.andy.feature.equipment.domain.Equipment;
import com.company.andy.feature.equipment.domain.EquipmentRepository;
import com.company.andy.feature.equipment.domain.event.EquipmentCreatedEvent;
import com.company.andy.feature.equipment.domain.event.EquipmentNameUpdatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.company.andy.RandomTestUtils.*;
import static com.company.andy.common.event.DomainEventType.EQUIPMENT_CREATED_EVENT;
import static com.company.andy.common.event.DomainEventType.EQUIPMENT_NAME_UPDATED_EVENT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EquipmentCommandServiceIntegrationTest extends IntegrationTest {
    @Autowired
    private EquipmentCommandService equipmentCommandService;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Test
    void should_create_equipment() {
        //Prepare data
        Operator operator = randomUserOperator();
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();

        //Execute
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, operator);

        //Verify results
        Equipment equipment = equipmentRepository.byId(equipmentId);
        assertEquals(createEquipmentCommand.name(), equipment.getName());
        assertEquals(operator.getOrgId(), equipment.getOrgId());

        // Verify domain events
        // Only need to check the existence of domain event in database,
        // no need to further test event handler as that will be handled in event handlers' own tests
        EquipmentCreatedEvent equipmentCreatedEvent = latestEventFor(equipmentId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        assertEquals(equipmentId, equipmentCreatedEvent.getEquipmentId());
    }

    @Test
    void should_update_equipment_name() {
        Operator operator = randomUserOperator();

        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, operator);

        UpdateEquipmentNameCommand updateEquipmentNameCommand = randomUpdateEquipmentNameCommand();
        equipmentCommandService.updateEquipmentName(equipmentId, updateEquipmentNameCommand, operator);

        Equipment equipment = equipmentRepository.byId(equipmentId);
        assertEquals(updateEquipmentNameCommand.name(), equipment.getName());
        EquipmentNameUpdatedEvent equipmentNameUpdatedEvent = latestEventFor(equipmentId, EQUIPMENT_NAME_UPDATED_EVENT, EquipmentNameUpdatedEvent.class);
        assertEquals(equipmentId, equipmentNameUpdatedEvent.getEquipmentId());
        assertEquals(updateEquipmentNameCommand.name(), equipmentNameUpdatedEvent.getUpdatedName());
    }
}
