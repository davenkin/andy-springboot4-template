package com.company.andy.common.model;

import com.company.andy.common.event.DomainEvent;
import com.company.andy.common.event.DomainEventType;
import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.security.CurrentActorService;
import org.junit.jupiter.api.Test;

import static com.company.andy.TestFixture.*;
import static org.junit.jupiter.api.Assertions.*;

class AggregateRootTest {

    @Test
    public void should_create_org_level_ar_with_org_id() {
        Actor actor = randomMemberActor();
        CurrentActorService.runWithActor(actor, () -> {
            String id = "some-id";
            AggregateRoot ar = new AggregateRoot(id, actor.requireOrgId()) {
            };

            assertEquals(id, ar.getId());
            assertEquals(actor.requireOrgId(), ar.getOrgId());
            assertNotNull(ar.getCreatedAt());
            assertNotNull(ar.getCreator());
            assertNotNull(ar.getCreatorId());
            assertNotNull(ar.getCreatorName());
        });
    }

    @Test
    void should_not_create_platform_level_ar_with_passed_org_id() {
        assertThrows(UnsupportedOperationException.class, () -> new AggregateRoot("some-id", randomOrgId()) {
            @Override
            protected boolean isPlatformObject() {
                return true;
            }
        });
    }

    @Test
    public void should_create_platform_level_ar() {
        Actor actor = randomSupervisorActor();
        CurrentActorService.runWithActor(actor, () -> {
            String id = "some-id";
            AggregateRoot ar = new AggregateRoot(id) {
                @Override
                protected boolean isPlatformObject() {
                    return true;
                }
            };

            assertEquals(id, ar.getId());
            assertNull(ar.getOrgId());
            assertNotNull(ar.getCreatedAt());
            assertNotNull(ar.getCreator());
            assertNotNull(ar.getCreatorId());
            assertNotNull(ar.getCreatorName());
        });
    }

    @Test
    void should_not_create_org_level_ar_without_passed_org_id() {
        assertThrows(UnsupportedOperationException.class, () -> new AggregateRoot("some-id") {
        });
    }

    @Test
    void should_raise_domain_event() {
        AggregateRoot ar = new AggregateRoot("some-id", randomOrgId()) {
        };

        assertNull(ar.getEvents());

        ar.raiseEvent(new DomainEvent(DomainEventType.EQUIPMENT_CREATED_EVENT, ar) {
        });

        assertEquals(1, ar.getEvents().size());

        ar.clearEvents();
        assertNull(ar.getEvents());
    }

}