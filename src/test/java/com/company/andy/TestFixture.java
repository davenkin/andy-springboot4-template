package com.company.andy;

import com.company.andy.common.model.Role;
import com.company.andy.common.model.actor.OrgActor;
import com.company.andy.common.model.actor.SystemActor;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Set;

import static com.company.andy.common.model.actor.ActorSource.HUMAN_USER;
import static com.company.andy.common.model.actor.SystemActor.createEventListenerSystemActor;
import static org.apache.commons.lang3.RandomUtils.secure;

public class TestFixture {
    public static SystemActor TEST_EVENT_LISTENER_ACTOR = createEventListenerSystemActor("some event listener");
    public static SystemActor TEST_JOB_ACTOR = createEventListenerSystemActor("some job");

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

    public static OrgActor randomHumanUserOrgActor() {
        return new OrgActor(randomUserId(), randomUserName(), randomOrgId(), Set.of(randomRole()), HUMAN_USER, "some initiator");
    }

    public static OrgActor randomHumanUserOrgActor(String actorId, String orgId) {
        return new OrgActor(actorId, randomUserName(), orgId, Set.of(randomRole()), HUMAN_USER, "some initiator");
    }

    public static <T extends Enum<T>> T randomEnum(Class<T> enumClass) {
        T[] constants = enumClass.getEnumConstants();
        return constants[secure().randomInt(0, constants.length)];
    }
}
