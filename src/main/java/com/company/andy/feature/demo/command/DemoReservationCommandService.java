package com.company.andy.feature.demo.command;

import com.company.andy.common.model.actor.AnonymousActor;
import com.company.andy.feature.demo.domain.DemoReservation;
import com.company.andy.feature.demo.domain.DemoReservationFactory;
import com.company.andy.feature.demo.domain.DemoReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// Command services handles command objects and orchestrates the processing flow
// Command services should not contain business logic but delegate to aggregate roots or domain services

@Slf4j
@Component
@RequiredArgsConstructor
public class DemoReservationCommandService {
  private final DemoReservationFactory demoReservationFactory;
  private final DemoReservationRepository demoReservationRepository;

  @Transactional
  public String createDemoReservation(CreateDemoReservationCommand command, AnonymousActor actor) {
    DemoReservation demoReservation = demoReservationFactory.createDemoReservation(command.mobileNumber(), actor);
    demoReservationRepository.save(demoReservation);
    log.info("Created DemoReservation[{}].", demoReservation.getId());
    return demoReservation.getId();
  }
}
