package com.company.andy.feature.equipment.controller;

import com.company.andy.IntegrationTest;
import com.company.andy.TestFixture;
import com.company.andy.common.model.actor.OrgActor;
import com.company.andy.common.util.PagedResponse;
import com.company.andy.common.util.ResponseId;
import com.company.andy.feature.equipment.command.CreateEquipmentCommand;
import com.company.andy.feature.equipment.command.EquipmentCommandService;
import com.company.andy.feature.equipment.command.UpdateEquipmentNameCommand;
import com.company.andy.feature.equipment.domain.Equipment;
import com.company.andy.feature.equipment.domain.EquipmentRepository;
import com.company.andy.feature.equipment.domain.EquipmentSummary;
import com.company.andy.feature.equipment.domain.event.EquipmentCreatedEvent;
import com.company.andy.feature.equipment.domain.event.EquipmentNameUpdatedEvent;
import com.company.andy.feature.equipment.query.PageEquipmentsQuery;
import com.company.andy.feature.equipment.query.QPagedEquipment;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.company.andy.common.event.DomainEventType.EQUIPMENT_CREATED_EVENT;
import static com.company.andy.common.event.DomainEventType.EQUIPMENT_NAME_UPDATED_EVENT;
import static com.company.andy.common.util.Constants.ORG_EQUIPMENTS_CACHE;
import static com.company.andy.feature.equipment.EquipmentTestFixture.*;
import static org.junit.jupiter.api.Assertions.*;

@NullMarked
class EquipmentControllerTest extends IntegrationTest {
    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private EquipmentCommandService equipmentCommandService;

    @Test
    void should_create_equipment() {
        // Prepare
        OrgActor actor = TestFixture.randomHumanUserOrgActor();
        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();

        // Execute
        ResponseId responseId = restTestClient.post()
                .uri("/equipments").headers(authHeaderOf(actor))
                .body(createEquipmentCommand)
                .exchange().expectStatus().isCreated()
                .expectBody(ResponseId.class).returnResult().getResponseBody();

        // Verify
        String equipmentId = responseId.id();
        Equipment equipment = equipmentRepository.byId(equipmentId);
        assertEquals(createEquipmentCommand.name(), equipment.getName());
        assertEquals(actor.getOrgId(), equipment.getOrgId());

        // Verify domain events
        // Only need to check the existence of domain event in database,
        // For event handling please add another test specifically for event handler
        EquipmentCreatedEvent equipmentCreatedEvent = latestEventFor(equipmentId, EQUIPMENT_CREATED_EVENT, EquipmentCreatedEvent.class);
        assertEquals(equipmentId, equipmentCreatedEvent.getEquipmentId());
    }

    @Test
    void should_update_equipment_name() {
        // Prepare
        OrgActor actor = TestFixture.randomHumanUserOrgActor();
        Consumer<HttpHeaders> authHeader = authHeaderOf(actor);

        CreateEquipmentCommand createEquipmentCommand = randomCreateEquipmentCommand();
        String equipmentId = equipmentCommandService.createEquipment(createEquipmentCommand, actor);

        // Execute
        UpdateEquipmentNameCommand updateEquipmentNameCommand = randomUpdateEquipmentNameCommand();
        restTestClient.put()
                .uri("/equipments/{id}/name", equipmentId).headers(authHeader)
                .body(updateEquipmentNameCommand)
                .exchange().expectStatus().isOk();

        // Verify
        Equipment equipment = equipmentRepository.byId(equipmentId);
        assertEquals(updateEquipmentNameCommand.name(), equipment.getName());

        // Verify domain events
        EquipmentNameUpdatedEvent equipmentNameUpdatedEvent = latestEventFor(equipmentId, EQUIPMENT_NAME_UPDATED_EVENT, EquipmentNameUpdatedEvent.class);
        assertEquals(equipmentId, equipmentNameUpdatedEvent.getEquipmentId());
        assertEquals(updateEquipmentNameCommand.name(), equipmentNameUpdatedEvent.getUpdatedName());
    }

    @Test
    void should_evict_org_equipment_summaries_cache_after_new_equipment_added() {
        // Prepare
        OrgActor actor = TestFixture.randomHumanUserOrgActor();
        Consumer<HttpHeaders> authHeader = authHeaderOf(actor);
        assertNull(cacheManager.getCache(ORG_EQUIPMENTS_CACHE).get(actor.getOrgId()));

        CreateEquipmentCommand createEquipmentCommand = new CreateEquipmentCommand(randomEquipmentName());
        equipmentCommandService.createEquipment(createEquipmentCommand, actor);

        assertNull(cacheManager.getCache(ORG_EQUIPMENTS_CACHE).get(actor.getOrgId()));
        List<EquipmentSummary> equipmentSummaries = equipmentRepository.cachedEquipmentSummaries(actor.getOrgId()).summaries();
        assertNotNull(equipmentSummaries);
        List<EquipmentSummary> cachedEquipmentSummaries = equipmentRepository.cachedEquipmentSummaries(actor.getOrgId()).summaries();
        assertNotNull(cachedEquipmentSummaries);
        assertNotNull(cacheManager.getCache(ORG_EQUIPMENTS_CACHE).get(actor.getOrgId()));

        // Execute
        // Create another equipment to evict the cache
        restTestClient.post()
                .uri("/equipments").headers(authHeader)
                .body(new CreateEquipmentCommand(randomEquipmentName()))
                .exchange().expectStatus().isCreated()
                .expectBody(ResponseId.class).returnResult().getResponseBody().id();


        // Verify
        assertNull(cacheManager.getCache(ORG_EQUIPMENTS_CACHE).get(actor.getOrgId()));
    }


    @Test
    void should_page_equipments() {
        // Prepare
        OrgActor actor = TestFixture.randomHumanUserOrgActor();
        Consumer<HttpHeaders> authHeader = authHeaderOf(actor);

        IntStream.range(0, 20).forEach(_ -> equipmentCommandService.createEquipment(randomCreateEquipmentCommand(), actor));

        // Execute
        PageEquipmentsQuery query = PageEquipmentsQuery.builder().pageSize(12).build();
        PagedResponse<QPagedEquipment> equipments = restTestClient.post()
                .uri("/equipments/paged").headers(authHeader)
                .body(query)
                .exchange().expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<PagedResponse<QPagedEquipment>>() {
                }).returnResult().getResponseBody();

        // Verify
        assertEquals(12, equipments.content().size());
    }
}