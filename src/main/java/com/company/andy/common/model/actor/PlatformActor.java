package com.company.andy.common.model.actor;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

// Actor representing an supervisor

@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator)
public class PlatformActor extends Actor {
    private String supervisorId;

    @Getter
    private Set<PlatformRole> roles;

    public PlatformActor(String id,
                         String name,
                         ActorType type,
                         String supervisorId,
                         Set<PlatformRole> roles,
                         PrincipalType principalType,
                         ActorOrigin origin) {
        super(id, name, type, principalType, origin);
        this.supervisorId = supervisorId;
        this.roles = roles;
    }

    public Optional<String> getSupervisorId() {
        return Optional.ofNullable(supervisorId);
    }
}
