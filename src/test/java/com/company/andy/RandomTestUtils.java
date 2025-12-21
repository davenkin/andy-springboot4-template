package com.company.andy;

import com.company.andy.common.model.Role;
import com.company.andy.common.model.operator.UserOperator;
import com.company.andy.sample.equipment.command.CreateEquipmentCommand;
import com.company.andy.sample.equipment.command.UpdateEquipmentHolderCommand;
import com.company.andy.sample.equipment.command.UpdateEquipmentNameCommand;
import com.company.andy.sample.equipment.domain.EquipmentStatus;
import com.company.andy.sample.maintenance.command.CreateMaintenanceRecordCommand;
import org.apache.commons.lang3.RandomStringUtils;

import static org.apache.commons.lang3.RandomUtils.secure;

public class RandomTestUtils {

    public static String randomEquipmentName() {
        return "EQUIPMENT_NAME_" + RandomStringUtils.secure().nextAlphanumeric(10);
    }

    public static String randomEquipmentHolderName() {
        return "HOLDER_NAME_" + RandomStringUtils.secure().nextAlphanumeric(10);
    }

    public static CreateEquipmentCommand randomCreateEquipmentCommand() {
        return new CreateEquipmentCommand(randomEquipmentName());
    }

    public static UpdateEquipmentNameCommand randomUpdateEquipmentNameCommand() {
        return new UpdateEquipmentNameCommand(randomEquipmentName());
    }

    public static UpdateEquipmentHolderCommand randomUpdateEquipmentHolderCommand() {
        return new UpdateEquipmentHolderCommand(randomEquipmentHolderName());
    }

    public static CreateMaintenanceRecordCommand randomCreateMaintenanceRecordCommand(String equipmentId) {
        return CreateMaintenanceRecordCommand.builder()
                .equipmentId(equipmentId)
                .description(randomDescription())
                .status(randomEquipmentStatus())
                .build();
    }

    public static String randomDescription() {
        return RandomStringUtils.secure().nextAscii(20);
    }

    public static String randomUserId() {
        return "USER_" + RandomStringUtils.secure().nextAlphanumeric(10);
    }

    public static EquipmentStatus randomEquipmentStatus() {
        return randomEnum(EquipmentStatus.class);
    }

    public static String randomUserName() {
        return "USER_NAME_" + RandomStringUtils.secure().nextAlphanumeric(10);
    }

    public static String randomOrgId() {
        return "ORG_" + RandomStringUtils.secure().nextAlphanumeric(10);
    }

    public static Role randomRole() {
        return randomEnum(Role.class);
    }

    public static UserOperator randomUserOperator() {
        return UserOperator.of(randomUserId(), randomUserName(), randomRole(), randomOrgId());
    }

    public static <T extends Enum<T>> T randomEnum(Class<T> enumClass) {
        T[] constants = enumClass.getEnumConstants();
        return constants[secure().randomInt(0, constants.length)];
    }
}
