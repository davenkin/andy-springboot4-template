package com.company.andy.common.model;

import com.company.andy.common.event.DomainEvent;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.model.actor.OrgActor;
import com.company.andy.common.model.actor.SystemActor;
import com.company.andy.feature.demoreservation.domain.DemoReservation;
import com.company.andy.feature.systemsettings.domain.SystemSettings;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.company.andy.common.utils.CommonUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

// Base class for all Aggregate Root objects.
// The AggregateRoot object stores raised domain events temporarily in memory,
// then the events will be persisted into DB by Repository within the same transaction that saves the AggregateRoot object.

@Getter
@FieldNameConstants
@NoArgsConstructor(access = PROTECTED)
public abstract class AggregateRoot {
    private static final Set<String> SYSTEM_LEVEL_OBJECT_CLASSES = Set.of(
                    SystemSettings.class,
                    DemoReservation.class)
            .stream().map(Class::getName).collect(toSet());
    private String id;
    private String orgId;

    // Domain events are stored temporarily in the Aggregate Root and are not persisted together with the entities as events will be stored in separately.
    // @Transient is very important for not persisting events with the Aggregate Root, otherwise we need to do this manually by ourselves.
    @Transient
    private List<DomainEvent> events;
    private Instant createdAt;
    private String createdBy;

    @Version
    @Getter(PRIVATE)
    private Long _version;

    // For OrgActor to create objects under the current org.
    protected AggregateRoot(String id, OrgActor actor) {
        checkOrgLevelObject();
        requireNonBlank(id, "id must not be blank.");
        requireNonNull(actor, "actor must not be null.");

        this.id = id;
        this.orgId = actor.getOrgId();
        this.createdAt = Instant.now();
        this.createdBy = actor.getId();
    }

    // For SystemActor to create objects under the specified org.
    protected AggregateRoot(String id, String orgId, SystemActor actor) {
        checkOrgLevelObject();
        requireNonBlank(id, "id must not be blank.");
        requireNonBlank(orgId, "orgId must not be blank.");
        requireNonNull(actor, "actor must not be null.");

        this.id = id;
        this.orgId = orgId;
        this.createdAt = Instant.now();
        this.createdBy = actor.getId();
    }

    protected AggregateRoot(String id, SystemActor actor) {
        checkSystemLevelObject();
        requireNonBlank(id, "id must not be blank.");
        requireNonNull(actor, "actor must not be null.");

        this.id = id;
        this.createdAt = Instant.now();
        this.createdBy = actor.getId();
    }

    // For any actor (including AnonymousActor) to create objects that don't belong to any org but the whole system.
    // Use this constructor with caution as it creates object without an orgId, which might not be what you want.
    protected AggregateRoot(String id, Actor actor) {
        checkSystemLevelObject();
        requireNonBlank(id, "id must not be blank.");
        requireNonNull(actor, "actor must not be null.");

        this.id = id;
        this.createdAt = Instant.now();
        this.createdBy = actor.getId();
    }

    // raiseEvent() only stores events in Aggregate Root temporarily,
    // the events will then be persisted into DB by Repository within the same transaction that saves the Aggregate Root object.
    // The actual sending of events to messaging middleware is handled by DomainEventPublishJob
    protected final void raiseEvent(DomainEvent event) {
        requireNonNull(event, "event must not be null.");
        requireNonNull(event.getType(), "event's type must not be null.");
        requireNonBlank(event.getArId(), "event's arId must not be null.");

        events().add(event);
    }

    private List<DomainEvent> events() {
        if (events == null) {
            this.events = new ArrayList<>();
        }

        return events;
    }

    public final void clearEvents() {
        this.events = null;
    }

    private void checkOrgLevelObject() {
        if (SYSTEM_LEVEL_OBJECT_CLASSES.contains(this.getClass().getName())) {
            throw new RuntimeException(this.getClass().getName() + " is not an org level Aggregate Root class.");
        }
    }

    private void checkSystemLevelObject() {
        if (!SYSTEM_LEVEL_OBJECT_CLASSES.contains(this.getClass().getName())) {
            throw new RuntimeException(this.getClass().getName() + " is not a system level Aggregate Root class.");
        }
    }

}
