package com.company.andy.feature.demoreservation.controller;

import static com.company.andy.TestFixture.randomAnonymousActor;
import static com.company.andy.TestFixture.randomHumanUserOrgActor;
import static com.company.andy.TestFixture.randomHumanUserSystemActor;
import static com.company.andy.common.event.DomainEventType.DEMO_RESERVATION_CREATED_EVENT;
import static com.company.andy.feature.demoreservation.DemoReservationTestFixture.randomDemoReservationCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.stream.IntStream;

import com.company.andy.IntegrationTest;
import com.company.andy.common.model.actor.AnonymousActor;
import com.company.andy.common.utils.PagedResponse;
import com.company.andy.common.utils.ResponseId;
import com.company.andy.feature.demoreservation.command.CreateDemoReservationCommand;
import com.company.andy.feature.demoreservation.command.DemoReservationCommandService;
import com.company.andy.feature.demoreservation.domain.DemoReservation;
import com.company.andy.feature.demoreservation.domain.DemoReservationRepository;
import com.company.andy.feature.demoreservation.domain.event.DemoReservationCreatedEvent;
import com.company.andy.feature.demoreservation.query.PageDemoReservationQuery;
import com.company.andy.feature.demoreservation.query.QPagedDemoReservation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;

class DemoReservationControllerTest extends IntegrationTest {
  @Autowired
  private DemoReservationRepository demoReservationRepository;

  @Autowired
  private DemoReservationCommandService demoReservationCommandService;

  @Test
  void should_create_demo_reservation() {
    // Prepare
    CreateDemoReservationCommand command = randomDemoReservationCommand();

    // Execute
    ResponseId responseId = restTestClient.post()
        .uri("/system/demo-reservations")
        .body(command)
        .exchange().expectStatus().isCreated()
        .expectBody(ResponseId.class).returnResult().getResponseBody();

    // Verify
    DemoReservation reservation = demoReservationRepository.byId(responseId.id());
    assertEquals(command.mobileNumber(), reservation.getMobileNumber());
    assertNull(reservation.getOrgId());

    // Verify domain event
    DemoReservationCreatedEvent createdEvent = latestEventFor(reservation.getId(),
        DEMO_RESERVATION_CREATED_EVENT,
        DemoReservationCreatedEvent.class);
    assertEquals(command.mobileNumber(), createdEvent.getMobileNumber());
  }

  @Test
  void should_page_demo_reservations() {
    // Prepare
    AnonymousActor anonymousActor = randomAnonymousActor();
    IntStream.range(0, 20)
        .forEach(_ -> demoReservationCommandService.createDemoReservation(randomDemoReservationCommand(), anonymousActor));

    // Execute
    PageDemoReservationQuery query = PageDemoReservationQuery.builder().pageSize(12).build();
    PagedResponse<QPagedDemoReservation> response = restTestClient.post()
        .uri("/system/demo-reservations/paged").headers(authHeaderOf(randomHumanUserSystemActor()))
        .body(query)
        .exchange().expectStatus().isOk()
        .expectBody(new ParameterizedTypeReference<PagedResponse<QPagedDemoReservation>>() {
        }).returnResult().getResponseBody();

    // Verify
    assertEquals(12, response.content().size());
  }

  @Test
  void org_user_should_not_allowed_to_page_demo_reservations() {
    restTestClient.post()
        .uri("/system/demo-reservations/paged").headers(authHeaderOf(randomHumanUserOrgActor()))
        .body(PageDemoReservationQuery.builder().pageSize(12).build())
        .exchange().expectStatus().isForbidden();
  }
}