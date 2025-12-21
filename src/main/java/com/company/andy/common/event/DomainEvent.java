package com.company.andy.common.event;

import com.company.andy.common.model.AggregateRoot;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.time.Instant;

import static com.company.andy.common.util.SnowflakeIdGenerator.newSnowflakeId;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PROTECTED;

// Base class for all domain events
@Getter
@FieldNameConstants
@NoArgsConstructor(access = PROTECTED)
public abstract class DomainEvent {
    private String id;
    private String arId;
    private String arOrgId;
    private DomainEventType type;
    private Instant raisedAt;
    private String raisedBy;

    protected DomainEvent(DomainEventType type, AggregateRoot ar) {
        requireNonNull(type, "type must not be null.");
        requireNonNull(ar, "ar must not be null.");

        this.id = newEventId();
        this.arId = ar.getId();
        this.arOrgId = ar.getOrgId();
        this.type = type;
        this.raisedAt = Instant.now();
    }

    public static String newEventId() {
        return "EVT" + newSnowflakeId();
    }

    public void raisedBy(String raisedBy) {
        this.raisedBy = raisedBy;
    }
}
