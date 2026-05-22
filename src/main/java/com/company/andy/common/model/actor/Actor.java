package com.company.andy.common.model.actor;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.time.Instant;

import static com.company.andy.common.utils.CommonUtils.requireNonBlank;
import static java.time.Instant.now;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PROTECTED;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = OrgActor.class, name = "ORG_ACTOR"),
        @JsonSubTypes.Type(value = SystemActor.class, name = "SYSTEM_ACTOR"),
        @JsonSubTypes.Type(value = AnonymousActor.class, name = "ANONYMOUS_ACTOR"),
})

@Getter
@FieldNameConstants
@NoArgsConstructor(access = PROTECTED)
public abstract class Actor {
    private String id;
    private String name;
    private ActorType type;
    private ActorSource source;
    private String initiator;
    private Instant createdAt;

    protected Actor(String id, String name, ActorType type, ActorSource source, String initiator) {
        requireNonBlank(id, "id must not be blank.");
        requireNonBlank(name, "name must not be blank.");
        requireNonNull(type, "type must not be null.");
        requireNonNull(source, "source must not be null.");
        requireNonBlank(initiator, "initiator must not be blank.");

        this.id = id;
        this.name = name;
        this.type = type;
        this.source = source;
        this.initiator = initiator;
        this.createdAt = now();
    }
}
