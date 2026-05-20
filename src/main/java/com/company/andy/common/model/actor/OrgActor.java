package com.company.andy.common.model.actor;

import com.company.andy.common.model.OrgRole;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.Set;

import static com.company.andy.common.model.actor.ActorType.ORG_ACTOR;
import static com.company.andy.common.util.CommonUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldNameConstants
@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator)
public class OrgActor extends Actor {
    private String orgId;
    private Set<OrgRole> roles;

    public OrgActor(String id, String name, String orgId, Set<OrgRole> roles, ActorSource source, String initiator) {
        requireNonBlank(orgId, "orgId must not be blank.");
        requireNonNull(roles, "roles must not be null.");

        super(id, name, ORG_ACTOR, source, initiator);
        this.orgId = orgId;
        this.roles = roles;
    }
}
