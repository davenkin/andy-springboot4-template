package com.company.andy.common.model.actor;

import static com.company.andy.common.model.Role.ORG_ADMIN;
import static com.company.andy.common.model.actor.ActorSource.BACKGROUND_JOB;
import static com.company.andy.common.model.actor.ActorSource.EVENT_LISTENER;
import static com.company.andy.common.model.actor.ActorSource.IMPERSONATED;
import static com.company.andy.common.model.actor.ActorType.SYSTEM_ACTOR;
import static com.company.andy.common.util.CommonUtils.requireNonBlank;
import static lombok.AccessLevel.PRIVATE;

import java.util.Set;

import com.company.andy.common.model.Role;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Getter
@FieldNameConstants
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

  public OrgActor impersonateOrg(String orgId) {
    return new OrgActor(getId(), getName(), orgId, Set.of(ORG_ADMIN), IMPERSONATED, getInitiator());
  }

  public OrgActor impersonateOrgUser(String userId, String name, Set<Role> roles, String orgId) {
    return new OrgActor(userId, name, orgId, roles, IMPERSONATED, getInitiator());
  }

  public static SystemActor createJobSystemActor(String jobName) {
    requireNonBlank(jobName, "jobName must not be blank.");

    return new SystemActor(BACKGROUND_JOB, "Job:[%s]".formatted(jobName));
  }

  public static SystemActor createEventListenerSystemActor(String listenerName) {
    return new SystemActor(EVENT_LISTENER, listenerName);
  }

  public static SystemActor createUserSystemActor(String id, String name, ActorSource source, String initiator) {
    return new SystemActor(id, name, source, initiator);
  }
}
