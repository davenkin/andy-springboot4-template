package com.company.andy;

import com.company.andy.common.model.Role;
import com.company.andy.common.model.actor.Actor;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Set;

import static com.company.andy.common.model.actor.ActorType.*;
import static org.apache.commons.lang3.RandomUtils.secure;

public class TestFixture {
    public static Actor TEST_EVENT_LISTENER_ACTOR = Actor.createPlatformActor(EVENT_LISTENER, "some event listener");
    public static Actor TEST_JOB_ACTOR = Actor.createPlatformActor(BACKGROUND_JOB, "some job");

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

    public static Actor randomOrgUserActor() {
        return Actor.createOrgActor(randomUserId(), randomUserName(), Set.of(randomRole()), randomOrgId(), HUMAN_USER, "some initiator");
    }

    public static Actor randomOrgUserActor(String userId, String orgId) {
        return Actor.createOrgActor(userId, randomUserName(), Set.of(randomRole()), orgId, HUMAN_USER, "some initiator");
    }

    public static <T extends Enum<T>> T randomEnum(Class<T> enumClass) {
        T[] constants = enumClass.getEnumConstants();
        return constants[secure().randomInt(0, constants.length)];
    }
}
