package com.company.andy.common.model.actor;

import com.company.andy.common.model.Role;

import java.time.Instant;
import java.util.Set;

import static com.company.andy.common.model.Role.PLATFORM;
import static com.company.andy.common.model.actor.ActorType.BACKGROUND_JOB;
import static com.company.andy.common.util.CommonUtils.requireNonBlank;
import static java.time.Instant.now;
import static java.util.Objects.requireNonNull;

public record Actor(String id,
                    String name,
                    Set<Role> roles,
                    String orgId,
                    ActorType type,
                    String initiator,
                    Instant createdAt) {
    public static final String PLATFORM_ACTOR_ID = "PLATFORM_ACTOR_001";
    public static final String PLATFORM_ACTOR_NAME = "PLATFORM_001";
    public static final String PLATFORM_ORG_ID = "PLATFORM_ORG_001";

    public static Actor createOrgActor(String id, String name, Set<Role> roles, String orgId, ActorType type, String initiator) {
        requireNonBlank(id, "id must not be blank.");
        requireNonBlank(name, "name must not be blank.");
        requireNonNull(roles, "roles must not be null.");
        requireNonBlank(orgId, "orgId must not be blank.");
        requireNonNull(type, "type must not be null.");
        requireNonBlank(initiator, "initiator must not be blank.");

        return new Actor(id, name, Set.copyOf(roles), orgId, type, initiator, now());
    }

    public static Actor createPlatformActor(ActorType type, String initiator) {
        requireNonNull(type, "type must not be null.");
        requireNonBlank(initiator, "initiator must not be blank.");

        return new Actor(PLATFORM_ACTOR_ID, PLATFORM_ACTOR_NAME, Set.of(PLATFORM), PLATFORM_ORG_ID, type, initiator, now());
    }

    public static Actor createJobActor(String jobName) {
        requireNonBlank(jobName, "jobName must not be blank.");
        return createPlatformActor(BACKGROUND_JOB, "Job:[%s]".formatted(jobName));
    }

    public boolean isOrgActor() {
        return !PLATFORM_ORG_ID.equals(this.orgId);
    }

}
