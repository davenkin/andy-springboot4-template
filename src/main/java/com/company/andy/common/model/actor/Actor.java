package com.company.andy.common.model.actor;

import com.company.andy.common.model.Role;

import java.util.Set;

import static com.company.andy.common.model.Role.PLATFORM;
import static com.company.andy.common.util.CommonUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;

public record Actor(String id,
                    String name,
                    Set<Role> roles,
                    String orgId,
                    ActorType source,
                    String initiator) {
    public static final String PLATFORM_ACTOR_ID = "PLATFORM_001";
    public static final String PLATFORM_ACTOR_NAME = "PLATFORM";
    public static final String PLATFORM_ORG_ID = "PLATFORM_001";

    public static Actor createOrgActor(String id, String name, Set<Role> roles, String orgId, ActorType source, String initiator) {
        requireNonBlank(id, "id must not be blank.");
        requireNonBlank(name, "name must not be blank.");
        requireNonNull(roles, "roles must not be null.");
        requireNonBlank(orgId, "orgId must not be blank.");
        requireNonNull(source, "source must not be null.");
        requireNonBlank(initiator, "initiator must not be blank.");

        return new Actor(id, name, Set.copyOf(roles), orgId, source, initiator);
    }

    public static Actor createPlatformActor(ActorType source, String initiator) {
        requireNonNull(source, "source must not be null.");
        requireNonBlank(initiator, "initiator must not be blank.");

        return new Actor(PLATFORM_ACTOR_ID, PLATFORM_ACTOR_NAME, Set.of(PLATFORM), PLATFORM_ORG_ID, source, initiator);
    }

    public boolean isOrgActor() {
        return !PLATFORM_ORG_ID.equals(this.orgId);
    }

}
