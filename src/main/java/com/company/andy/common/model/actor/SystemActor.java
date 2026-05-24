package com.company.andy.common.model.actor;

import com.company.andy.common.model.OrgRole;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

import static com.company.andy.common.model.actor.ActorSource.*;
import static com.company.andy.common.model.actor.ActorType.SYSTEM_ACTOR;
import static com.company.andy.common.utils.CommonUtils.requireNonBlank;
import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator)
public class SystemActor extends Actor {
    public static final String SYSTEM_ACTOR_ID = "SYSTEM_ACTOR_001";
    public static final String SYSTEM_ACTOR_NAME = "SYSTEM_ACTOR_001";

    private SystemActor(ActorSource source, String initiator) {
        super(SYSTEM_ACTOR_ID, SYSTEM_ACTOR_NAME, SYSTEM_ACTOR, source, initiator);
    }

    private SystemActor(String id, String name, ActorSource source, String initiator) {
        super(id, name, SYSTEM_ACTOR, source, initiator);
    }

    public static SystemActor createJobSystemActor(String jobName) {
        requireNonBlank(jobName, "jobName must not be blank.");

        return new SystemActor(BACKGROUND_JOB, "Job:[%s]".formatted(jobName));
    }

    public static SystemActor createEventListenerSystemActor(String listenerName) {
        return new SystemActor(EVENT_HANDLER, listenerName);
    }

    public static SystemActor createUserSystemActor(String id, String name, ActorSource source, String initiator) {
        return new SystemActor(id, name, source, initiator);
    }

    public OrgActor impersonateOrgActor(String impersonatedActorId, String name, Set<OrgRole> roles, String orgId) {
        return new OrgActor(impersonatedActorId, name, orgId, roles, IMPERSONATED, getInitiator());
    }
}
