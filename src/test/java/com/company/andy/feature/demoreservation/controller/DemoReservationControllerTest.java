package com.company.andy.feature.demoreservation.controller;

import static com.company.andy.common.event.DomainEventType.DEMO_RESERVATION_CREATED_EVENT;
import static com.company.andy.feature.demoreservation.DemoReservationTestFixture.randomDemoReservationCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.company.andy.IntegrationTest;
import com.company.andy.common.utils.ResponseId;
import com.company.andy.feature.demoreservation.command.CreateDemoReservationCommand;
import com.company.andy.feature.demoreservation.domain.DemoReservation;
import com.company.andy.feature.demoreservation.domain.DemoReservationRepository;
import com.company.andy.feature.demoreservation.domain.event.DemoReservationCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DemoReservationControllerTest extends IntegrationTest {
  @Autowired
  private DemoReservationRepository demoReservationRepository;

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
}