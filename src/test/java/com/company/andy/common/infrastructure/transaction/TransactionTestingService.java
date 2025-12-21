package com.company.andy.common.infrastructure.transaction;

import com.company.andy.common.model.operator.Operator;
import com.company.andy.sample.equipment.command.EquipmentCommandService;
import com.company.andy.sample.equipment.command.UpdateEquipmentHolderCommand;
import com.company.andy.sample.equipment.command.UpdateEquipmentNameCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TransactionTestingService {
    private final EquipmentCommandService equipmentCommandService;

    @Transactional
    public void throwExceptionWithTransaction(String equipmentId,
                                              UpdateEquipmentNameCommand nameCommand,
                                              UpdateEquipmentHolderCommand holderCommand,
                                              Operator operator) {
        equipmentCommandService.updateEquipmentName(equipmentId, nameCommand, operator);
        equipmentCommandService.updateEquipmentHolder(equipmentId, holderCommand, operator);
        throw new RuntimeException("fake exception"); // mimic exception at the end of transaction flow
    }


    public void throwExceptionAtTheEndWithoutTransaction(String equipmentId,
                                                         UpdateEquipmentNameCommand nameCommand,
                                                         UpdateEquipmentHolderCommand holderCommand,
                                                         Operator operator) {
        equipmentCommandService.updateEquipmentName(equipmentId, nameCommand, operator);
        equipmentCommandService.updateEquipmentHolder(equipmentId, holderCommand, operator);
        throw new RuntimeException("fake exception");
    }

    public void throwExceptionInTheMiddleWithoutTransaction(String equipmentId,
                                                            UpdateEquipmentNameCommand nameCommand,
                                                            UpdateEquipmentHolderCommand holderCommand,
                                                            Operator operator) {
        equipmentCommandService.updateEquipmentName(equipmentId, nameCommand, operator);
        if (true) { // mimic exception in the middle of the transaction flow
            throw new RuntimeException("fake exception");
        }
        equipmentCommandService.updateEquipmentHolder(equipmentId, holderCommand, operator);
    }

}
