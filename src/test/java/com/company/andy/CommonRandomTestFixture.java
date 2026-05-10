package com.company.andy;

import com.company.andy.common.model.Role;
import com.company.andy.common.model.operator.Operator;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Set;

import static com.company.andy.common.model.operator.OperatorSource.*;
import static org.apache.commons.lang3.RandomUtils.secure;

public class CommonRandomTestFixture {
    public static Operator TEST_EVENT_LISTENER_OPERATOR = Operator.createPlatformOperator(EVENT_LISTENER, "some event listener");
    public static Operator TEST_JOB_OPERATOR = Operator.createPlatformOperator(BACKGROUND_JOB, "some job");

    public static String randomDescription() {
        return RandomStringUtils.secure().nextAscii(20);
    }

    public static String randomUserId() {
        return "USER_" + RandomStringUtils.secure().nextAlphanumeric(10);
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

    public static Operator randomOrgUserOperator() {
        return Operator.createOrgOperator(randomUserId(), randomUserName(), Set.of(randomRole()), randomOrgId(), HUMAN_USER, "some initiator");
    }

    public static Operator randomOrgUserOperator(String userId, String orgId) {
        return Operator.createOrgOperator(userId, randomUserName(), Set.of(randomRole()), orgId, HUMAN_USER, "some initiator");
    }

    public static Operator randomPlatformOperator() {
        return Operator.createPlatformOperator(BACKGROUND_JOB, "some initiator");
    }

    public static <T extends Enum<T>> T randomEnum(Class<T> enumClass) {
        T[] constants = enumClass.getEnumConstants();
        return constants[secure().randomInt(0, constants.length)];
    }
}
