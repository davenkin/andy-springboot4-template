package com.company.andy.feature.demoreservation.command;

import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.ratelimiter.RateLimiter;
import com.company.andy.feature.demoreservation.domain.DemoReservation;
import com.company.andy.feature.demoreservation.domain.DemoReservationFactory;
import com.company.andy.feature.demoreservation.domain.DemoReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// Command services handles command objects and orchestrates the processing flow
// Command services should not contain business logic but delegate to Aggregate Roots or domain services

@Slf4j
@Component
@RequiredArgsConstructor
public class DemoReservationCommandService {
    private final DemoReservationFactory demoReservationFactory;
    private final DemoReservationRepository demoReservationRepository;
    private final RateLimiter rateLimiter;

    @Transactional
    public String createDemoReservation(CreateDemoReservationCommand command, Actor actor) {
        rateLimiter.applyFor("create_demo_reservation", 5);
        DemoReservation demoReservation = demoReservationFactory.createDemoReservation(command.mobileNumber(), actor);
        demoReservationRepository.save(demoReservation);
        log.info("Created DemoReservation[{}].", demoReservation.getId());
        return demoReservation.getId();
    }
}
