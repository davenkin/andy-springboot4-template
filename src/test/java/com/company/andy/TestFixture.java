package com.company.andy;

import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.model.actor.ActorOrigin;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.UUID;

import static org.apache.commons.lang3.RandomUtils.secure;

public class TestFixture {
    public static final String X_TEST_ID = "x-test-id";
    public static ActorOrigin RANDOM_ACTOR_ORIGIN = ActorOrigin.fromRobot("some-fake-origin");

    public static String randomDescription() {
        return RandomStringUtils.secure().nextAscii(20);
    }

    public static String randomMemberId() {
        return "MBR" + RandomStringUtils.secure().nextAlphanumeric(10);
    }

    public static String randomServiceClientId() {
        return "SCL" + RandomStringUtils.secure().nextAlphanumeric(10);
    }

    public static String randomSupervisorId() {
        return "SPV" + RandomStringUtils.secure().nextAlphanumeric(10);
    }

    public static String randomUserName() {
        return "UNM" + RandomStringUtils.secure().nextAlphanumeric(5);
    }

    public static String randomOrgId() {
        return "ORG" + RandomStringUtils.secure().nextAlphanumeric(10);
    }

    public static String randomScheduledJobName() {
        return "ScheduledJob:" + RandomStringUtils.secure().nextAlphanumeric(10);
    }

    public static Actor randomMemberActor() {
        return Actor.createMemberActor(randomMemberId(), randomUserName(), randomOrgId(), RANDOM_ACTOR_ORIGIN);
    }

    public static Actor randomSupervisorActor() {
        return Actor.createSupervisorActor(randomSupervisorId(), randomUserName(), RANDOM_ACTOR_ORIGIN);
    }

    public static Actor randomOrgServiceClientActor() {
        return Actor.createOrgServiceClientActor(randomServiceClientId(), randomOrgId(), RANDOM_ACTOR_ORIGIN);
    }

    public static Actor randomPlatformServiceClientActor() {
        return Actor.createPlatformServiceClientActor(randomServiceClientId(), RANDOM_ACTOR_ORIGIN);
    }

    public static Actor randomAnonymousActor() {
        return Actor.createAnonymousActor(RANDOM_ACTOR_ORIGIN);
    }

    public static Actor randomScheduledJobRobotActor() {
        return Actor.createRobotActor(randomScheduledJobName());
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
