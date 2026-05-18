package com.company.andy.common.model.actor;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.Set;

import static com.company.andy.common.model.Role.ORG_ADMIN;
import static com.company.andy.common.model.actor.ActorSource.BACKGROUND_JOB;
import static com.company.andy.common.model.actor.ActorType.SYSTEM_ACTOR;
import static com.company.andy.common.util.CommonUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldNameConstants
@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator)
public class SystemActor extends Actor {
    public static final String SYSTEM_ACTOR_ID = "ACTOR_001";
    public static final String SYSTEM_ACTOR_NAME = "ACTOR_001";

    private SystemActor(ActorSource source, String initiator) {
        super(SYSTEM_ACTOR_ID, SYSTEM_ACTOR_NAME, SYSTEM_ACTOR, source, initiator);
    }

    public OrgActor impersonateOrg(String orgId) {
        requireNonBlank(orgId, "orgId must not be blank.");

        return new OrgActor(getId(), getName(), orgId, Set.of(ORG_ADMIN), getSource(), getInitiator());
    }

    public static SystemActor createJobActor(String jobName) {
        requireNonBlank(jobName, "jobName must not be blank.");

        return new SystemActor(BACKGROUND_JOB, "Job:[%s]".formatted(jobName));
    }

    public static SystemActor createSystemActor(ActorSource source, String initiator) {
        requireNonNull(source, "source must not be null.");
        requireNonBlank(initiator, "initiator must not be blank.");

        return new SystemActor(source, initiator);
    }

}
