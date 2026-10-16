package com.company.andy.common.model.actor;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.function.Consumer;

import static com.company.andy.common.model.actor.ActorOrigin.fromRobot;
import static com.company.andy.common.model.actor.ActorOrigin.fromScheduledJob;
import static com.company.andy.common.model.actor.ActorType.*;
import static com.company.andy.common.utils.CommonUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator)
public class Actor {
    @Getter
    private String id;

    @Getter
    private String name;

    @Getter
    private ActorType type;

    @Getter
    private ActorOrigin origin;

    private String orgId;

    @Getter
    private Instant createdAt;

    private Actor(String id, String name, ActorType type, String orgId, ActorOrigin origin) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.orgId = orgId;
        this.origin = origin;
        this.createdAt = Instant.now();
    }

    public static Actor createMemberActor(String memberId,
                                          String name,
                                          String orgId,
                                          ActorOrigin origin) {
        requireNonBlank(memberId, "memberId must not be blank.");
        requireNonBlank(name, "name must not be blank.");
        requireNonBlank(orgId, "orgId must not be blank.");
        requireNonNull(origin, "origin must not be null.");

        return new Actor(memberId, name, MEMBER, orgId, origin);
    }

    public static Actor createOrgServiceClientActor(String clientId,
                                                    String orgId,
                                                    ActorOrigin origin) {
        requireNonBlank(clientId, "clientId must not be blank.");
        requireNonBlank(orgId, "orgId must not be blank.");
        requireNonNull(origin, "origin must not be null.");

        return new Actor(clientId, null, ORG_SERVICE_CLIENT, orgId, origin);
    }

    public static Actor createSupervisorActorForOrg(String supervisorId,
                                                    String name,
                                                    String orgId,
                                                    ActorOrigin origin) {
        requireNonBlank(supervisorId, "supervisorId must not be blank.");
        requireNonBlank(name, "name must not be blank.");
        requireNonBlank(orgId, "orgId must not be blank.");
        requireNonNull(origin, "origin must not be null.");

        return new Actor(supervisorId, name, SUPERVISOR, orgId, origin);
    }

    public static Actor createPlatformServiceClientActorForOrg(String clientId,
                                                               String orgId,
                                                               ActorOrigin origin) {
        requireNonBlank(clientId, "clientId must not be blank.");
        requireNonBlank(orgId, "orgId must not be blank.");
        requireNonNull(origin, "origin must not be null.");

        return new Actor(clientId, null, PLATFORM_SERVICE_CLIENT, orgId, origin);
    }


    public static Actor createSupervisorActor(String supervisorId,
                                              String name,
                                              ActorOrigin origin) {
        requireNonBlank(supervisorId, "supervisorId must not be blank.");
        requireNonBlank(name, "name must not be blank.");
        requireNonNull(origin, "origin must not be null.");

        return new Actor(supervisorId, name, SUPERVISOR, null, origin);
    }

    public static Actor createPlatformServiceClientActor(String clientId, ActorOrigin origin) {
        requireNonBlank(clientId, "clientId must not be blank.");
        requireNonNull(origin, "origin must not be null.");

        return new Actor(clientId, null, PLATFORM_SERVICE_CLIENT, null, origin);
    }


    public static Actor createScheduledJobActor(String jobName) {
        requireNonBlank(jobName, "jobName must not be blank.");

        return new Actor(jobName, null, ROBOT, null, fromScheduledJob(jobName));
    }

    public static Actor createEventHandlerActor(String handlerName, ActorOrigin origin) {
        requireNonBlank(handlerName, "handlerName must not be blank.");
        requireNonNull(origin, "origin must not be null.");

        return new Actor(handlerName, null, ROBOT, null, origin);
    }

    public static Actor createRobotActor(String robotName) {
        requireNonBlank(robotName, "robotName must not be blank.");

        return new Actor(robotName, null, ROBOT, null, fromRobot(robotName));
    }

    public static Actor createDeviceActor(String deviceId, String orgId, ActorOrigin origin) {
        requireNonBlank(deviceId, "deviceId must not be blank.");
        requireNonBlank(orgId, "orgId must not be blank.");

        return new Actor(deviceId, null, DEVICE, orgId, origin);
    }

    public static Actor createAnonymousActor(ActorOrigin origin) {
        requireNonNull(origin, "origin must not be null.");

        return new Actor(ANONYMOUS.name(), null, ANONYMOUS, null, origin);
    }

    public void ifMember(Consumer<String> memberIdConsumer) {
        if (this.getType() == MEMBER) {
            memberIdConsumer.accept(this.id);
        }
    }

    public String requireMemberId() {
        if (this.getType() == MEMBER) {
            return this.id;
        } else {
            throw new IllegalStateException("ActorType is not MEMBER but " + this.getType() + ".");
        }
    }

    public void ifSupervisor(Consumer<String> supervisorIdConsumer) {
        if (this.getType() == SUPERVISOR) {
            supervisorIdConsumer.accept(this.id);
        }
    }

    public String requireSupervisorId() {
        if (this.getType() == SUPERVISOR) {
            return this.id;
        } else {
            throw new IllegalStateException("ActorType is not SUPERVISOR but " + this.getType() + ".");
        }
    }

    public void ifDevice(Consumer<String> deviceIdConsumer) {
        if (this.getType() == DEVICE) {
            deviceIdConsumer.accept(this.id);
        }
    }

    public String requireDeviceId() {
        if (this.getType() == DEVICE) {
            return this.id;
        } else {
            throw new IllegalStateException("ActorType is not DEVICE but " + this.getType() + ".");
        }
    }

    public void ifOrgActor(Consumer<String> orgIdConsumer) {
        if (isNotBlank(this.orgId)) {
            orgIdConsumer.accept(this.orgId);
        }
    }

    public String requireOrgId() {
        if (isNotBlank(this.orgId)) {
            return this.orgId;
        } else {
            throw new IllegalStateException("orgId is required but no value presents.");
        }
    }

}
