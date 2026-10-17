package com.company.andy.common.model.actor;

import org.junit.jupiter.api.Test;

import static com.company.andy.TestFixture.*;
import static com.company.andy.common.model.actor.ActorType.*;
import static org.junit.jupiter.api.Assertions.*;

class ActorTest {

    @Test
    void should_create_member_actor() {
        String memberId = randomMemberId();
        String memberName = randomUserName();
        String orgId = randomOrgId();

        Actor actor = Actor.createMemberActor(memberId, memberName, orgId, RANDOM_ACTOR_ORIGIN);

        assertEquals(memberId, actor.getId());
        assertEquals(memberId, actor.requireMemberId());
        assertEquals(memberName, actor.getName());
        assertEquals(orgId, actor.requireOrgId());
        assertEquals(MEMBER, actor.getType());
        assertNotNull(actor.getCreatedAt());
        assertEquals(RANDOM_ACTOR_ORIGIN, actor.getOrigin());
    }

    @Test
    void should_create_org_service_client_actor() {
        String clientId = randomServiceClientId();
        String orgId = randomOrgId();

        Actor actor = Actor.createOrgServiceClientActor(clientId, orgId, RANDOM_ACTOR_ORIGIN);

        assertEquals(clientId, actor.getId());
        assertEquals(clientId, actor.requireServiceClientId());
        assertEquals(orgId, actor.requireOrgId());
        assertEquals(ORG_SERVICE_CLIENT, actor.getType());
        assertNull(actor.getName());
        assertNotNull(actor.getCreatedAt());
        assertEquals(RANDOM_ACTOR_ORIGIN, actor.getOrigin());
    }

    @Test
    void should_create_supervisor_actor_for_org() {
        String supervisorId = randomSupervisorId();
        String name = randomUserName();
        String orgId = randomOrgId();

        Actor actor = Actor.createSupervisorActorForOrg(supervisorId, name, orgId, RANDOM_ACTOR_ORIGIN);

        assertEquals(supervisorId, actor.getId());
        assertEquals(supervisorId, actor.requireSupervisorId());
        assertEquals(orgId, actor.requireOrgId());
        assertEquals(SUPERVISOR, actor.getType());
        assertEquals(name, actor.getName());
        assertNotNull(actor.getCreatedAt());
        assertEquals(RANDOM_ACTOR_ORIGIN, actor.getOrigin());
    }

    @Test
    void should_create_platform_actor_for_org() {
        String clientId = randomServiceClientId();
        String orgId = randomOrgId();

        Actor actor = Actor.createPlatformServiceClientActorForOrg(clientId, orgId, RANDOM_ACTOR_ORIGIN);

        assertEquals(clientId, actor.getId());
        assertEquals(clientId, actor.requireServiceClientId());
        assertEquals(orgId, actor.requireOrgId());
        assertEquals(PLATFORM_SERVICE_CLIENT, actor.getType());
        assertNull(actor.getName());
        assertNotNull(actor.getCreatedAt());
        assertEquals(RANDOM_ACTOR_ORIGIN, actor.getOrigin());
    }

    @Test
    void should_create_supervisor_actor() {
        String supervisorId = randomSupervisorId();
        String name = randomUserName();

        Actor actor = Actor.createSupervisorActor(supervisorId, name, RANDOM_ACTOR_ORIGIN);

        assertEquals(supervisorId, actor.getId());
        assertEquals(supervisorId, actor.requireSupervisorId());
        assertThrows(IllegalStateException.class, actor::requireOrgId);
        assertEquals(SUPERVISOR, actor.getType());
        assertEquals(name, actor.getName());
        assertNotNull(actor.getCreatedAt());
        assertEquals(RANDOM_ACTOR_ORIGIN, actor.getOrigin());
    }

    @Test
    void should_create_platform_service_client_actor() {
        String clientId = randomServiceClientId();

        Actor actor = Actor.createPlatformServiceClientActor(clientId, RANDOM_ACTOR_ORIGIN);

        assertEquals(clientId, actor.getId());
        assertEquals(clientId, actor.requireServiceClientId());
        assertThrows(IllegalStateException.class, actor::requireOrgId);
        assertEquals(PLATFORM_SERVICE_CLIENT, actor.getType());
        assertNull(actor.getName());
        assertNotNull(actor.getCreatedAt());
        assertEquals(RANDOM_ACTOR_ORIGIN, actor.getOrigin());
    }

    @Test
    void should_create_scheduled_job_actor() {
        String jobName = randomScheduledJobName();

        Actor actor = Actor.createScheduledJobActor(jobName);

        assertEquals(jobName, actor.getId());
        assertThrows(IllegalStateException.class, actor::requireOrgId);
        assertEquals(ROBOT, actor.getType());
        assertNull(actor.getName());
        assertNotNull(actor.getCreatedAt());
        assertNotNull(actor.getOrigin());
    }

    @Test
    void should_create_event_handler_actor() {
        String handlerName = randomEventHandlerName();

        Actor actor = Actor.createEventHandlerActor(handlerName, RANDOM_ACTOR_ORIGIN);

        assertEquals(handlerName, actor.getId());
        assertThrows(IllegalStateException.class, actor::requireOrgId);
        assertEquals(ROBOT, actor.getType());
        assertNull(actor.getName());
        assertNotNull(actor.getCreatedAt());
        assertNotNull(actor.getOrigin());
    }

    @Test
    void should_create_robot_actor() {
        String robotName = randomRobotName();

        Actor actor = Actor.createRobotActor(robotName);

        assertEquals(robotName, actor.getId());
        assertThrows(IllegalStateException.class, actor::requireOrgId);
        assertEquals(ROBOT, actor.getType());
        assertNull(actor.getName());
        assertNotNull(actor.getCreatedAt());
        assertNotNull(actor.getOrigin());
    }

    @Test
    void should_create_device_actor() {
        String deviceId = randomDeviceId();
        String orgId = randomOrgId();

        Actor actor = Actor.createDeviceActor(deviceId, orgId, RANDOM_ACTOR_ORIGIN);

        assertEquals(deviceId, actor.getId());
        assertEquals(deviceId, actor.requireDeviceId());
        assertNull(actor.getName());
        assertEquals(orgId, actor.requireOrgId());
        assertEquals(DEVICE, actor.getType());
        assertNotNull(actor.getCreatedAt());
        assertEquals(RANDOM_ACTOR_ORIGIN, actor.getOrigin());
    }

    @Test
    void should_create_anonymous_actor() {
        Actor actor = Actor.createAnonymousActor(RANDOM_ACTOR_ORIGIN);

        assertEquals(ANONYMOUS.name(), actor.getId());
        assertThrows(IllegalStateException.class, actor::requireOrgId);
        assertEquals(ANONYMOUS, actor.getType());
        assertNull(actor.getName());
        assertNotNull(actor.getCreatedAt());
        assertNotNull(actor.getOrigin());
    }

}