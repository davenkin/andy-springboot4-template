package com.company.andy;

import com.company.andy.common.model.OrgRole;
import com.company.andy.common.model.actor.AnonymousActor;
import com.company.andy.common.model.actor.OrgActor;
import com.company.andy.common.model.actor.SystemActor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.Set;
import java.util.UUID;

import static com.company.andy.common.model.actor.ActorSource.HUMAN_USER;
import static com.company.andy.common.model.actor.AnonymousActor.createAnonymousActor;
import static com.company.andy.common.model.actor.SystemActor.createEventListenerSystemActor;
import static com.company.andy.common.model.actor.SystemActor.createUserSystemActor;
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

    public static OrgRole randomRole() {
        return randomEnum(OrgRole.class);
    }

    public static OrgActor randomHumanUserOrgActor(OrgRole role) {
        return new OrgActor(randomUserId(), randomUserName(), randomOrgId(), Set.of(role), HUMAN_USER, "some initiator");
    }

    public static SystemActor randomHumanUserSystemActor() {
        return createUserSystemActor(randomUserId(), randomUserName(), HUMAN_USER, "some initiator");
    }

    public static AnonymousActor randomAnonymousActor() {
        return createAnonymousActor("some initiator");
    }

    public static <T extends Enum<T>> T randomEnum(Class<T> enumClass) {
        T[] constants = enumClass.getEnumConstants();
        return constants[secure().randomInt(0, constants.length)];
    }

    public static String randomExternalEventId() {
        return UUID.randomUUID().toString();
    }

    public static String randomMobileNumber() {
        return String.valueOf(RandomUtils.secure().randomLong(13000000000L, 19000000000L));
    }
}
