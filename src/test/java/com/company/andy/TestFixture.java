package com.company.andy;

import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.model.actor.ActorOrigin;
import com.company.andy.common.model.actor.OrgActor;
import com.company.andy.common.model.actor.PlatformActor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.Set;
import java.util.UUID;

import static org.apache.commons.lang3.RandomUtils.secure;

public class TestFixture {
    public static ActorOrigin RANDOM_ACTOR_ORIGIN = ActorOrigin.fromInitialization("some-fake-origin");

    public static String randomDescription() {
        return RandomStringUtils.secure().nextAscii(20);
    }

    public static String randomMemberId() {
        return "MBR" + RandomStringUtils.secure().nextAlphanumeric(10);
    }

    public static String randomSupervisorId() {
        return "SUP" + RandomStringUtils.secure().nextAlphanumeric(10);
    }

    public static String randomUserName() {
        return "USERNAME_" + RandomStringUtils.secure().nextAlphanumeric(5);
    }

    public static String randomOrgId() {
        return "ORG_" + RandomStringUtils.secure().nextAlphanumeric(10);
    }

    public static OrgActor randomMemberActor() {
        return Actor.createMemberActor(randomMemberId(), randomUserName(), randomOrgId(), Set.of(), RANDOM_ACTOR_ORIGIN);
    }

    public static PlatformActor randomSupervisorActor() {
        return Actor.createSupervisorActor(randomSupervisorId(), randomUserName(), Set.of(), RANDOM_ACTOR_ORIGIN);
    }

    public static PlatformActor randomAnonymousActor() {
        return Actor.createAnonymousActor(RANDOM_ACTOR_ORIGIN);
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

    public static String randomEmail() {
        String local = RandomStringUtils.secure().nextAlphanumeric(10).toLowerCase();
        String domain = RandomStringUtils.secure().nextAlphanumeric(5).toLowerCase();
        return local + "@" + domain + ".com";
    }
}
