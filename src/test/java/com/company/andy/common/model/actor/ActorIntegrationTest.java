package com.company.andy.common.model.actor;

import com.company.andy.IntegrationTest;
import com.company.andy.common.security.CurrentActorService;
import com.company.andy.common.utils.ResponseId;
import com.company.andy.feature.equipment.domain.Equipment;
import com.company.andy.feature.equipment.domain.EquipmentRepository;
import com.company.andy.feature.equipment.domain.event.EquipmentCreatedEvent;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;

import java.util.concurrent.CompletableFuture;

import static com.company.andy.TestFixture.randomMemberActor;
import static com.company.andy.common.event.DomainEventType.EQUIPMENT_CREATED_EVENT;
import static com.company.andy.common.security.CurrentActorService.runWithActor;
import static com.company.andy.common.utils.Constants.APPLICATION_TASK_EXECUTOR;
import static com.company.andy.common.utils.Constants.THREAD_POOL_TASK_EXECUTOR;
import static com.company.andy.feature.equipment.EquipmentTestFixture.randomCreateEquipmentCommand;
import static org.junit.jupiter.api.Assertions.*;

public class ActorIntegrationTest extends IntegrationTest {
    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    @Qualifier(APPLICATION_TASK_EXECUTOR)
    private TaskExecutor applicationTaskExecutor;

    @Autowired
    @Qualifier(THREAD_POOL_TASK_EXECUTOR)
    private TaskExecutor threadPoolTaskExecutor;

    @Test
    void actor_should_be_recorded_in_aggregate_root() {
        Actor actor = randomMemberActor();

        String equipmentId = restTestClient.post()
                .uri("/equipments")
                .headers(authHeaderOf(actor))
                .body(randomCreateEquipmentCommand())
                .exchange().expectStatus().isCreated()
                .expectBody(ResponseId.class).returnResult().getResponseBody().id();

        Equipment equipment = equipmentRepository.byId(equipmentId);
        assertEquals(actor.requireOrgId(), equipment.getOrgId());

        assertEquals(actor.getId(), equipment.getCreatorId());
        assertEquals(actor.getName(), equipment.getCreatorName());
        assertEquals(actor.getId(), equipment.getCreator().getId());
    }

    @Test
    void actor_should_be_recorded_in_domain_events() {
        Actor actor = randomMemberActor();

        String equipmentId = restTestClient.post()
                .uri("/equipments")
                .headers(authHeaderOf(actor))
                .body(randomCreateEquipmentCommand())
                .exchange().expectStatus().isCreated()
                .expectBody(ResponseId.class).returnResult().getResponseBody().id();

        Equipment equipment = equipmentRepository.byId(equipmentId);
        assertEquals(actor.requireOrgId(), equipment.getOrgId());
        EquipmentCreatedEvent equipmentCreatedEvent = latestDomainEventFor(equipmentId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        assertEquals(actor.getId(), equipmentCreatedEvent.getRaisedBy().getId());
    }

    @Test
    void actor_should_be_passed_to_another_thread_by_thread_pool_task_executor() {
        Actor actor = randomMemberActor();

        runWithActor(actor, () -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Actor currentActor = CurrentActorService.currentActor().orElseThrow();
                assertEquals(actor.getId(), currentActor.getId());
                assertTrue(MDC.get("actor").contains(actor.getId()));
                assertEquals(actor.requireOrgId(), MDC.get("actorOrgId"));
                assertNotNull(MDC.get("actorOrigin"));
            }, threadPoolTaskExecutor);
            future.join();
        });
    }

    @Test
    void actor_should_be_passed_to_another_thread_by_application_task_executor() {
        Actor actor = randomMemberActor();

        runWithActor(actor, () -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                Actor currentActor = CurrentActorService.currentActor().orElseThrow();
                assertEquals(actor.getId(), currentActor.getId());
                assertTrue(MDC.get("actor").contains(actor.getId()));
                assertEquals(actor.requireOrgId(), MDC.get("actorOrgId"));
                assertNotNull(MDC.get("actorOrigin"));
            }, applicationTaskExecutor);
            future.join();
        });
    }

}
