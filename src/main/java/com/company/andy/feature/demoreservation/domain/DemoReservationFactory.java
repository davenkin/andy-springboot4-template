package com.company.andy.feature.demoreservation.domain;

import com.company.andy.common.model.actor.Actor;
import org.springframework.stereotype.Component;

// Always use factories to create aggregate root objects,
// which makes the creation process of aggregate roots more explicit

@Component
public class DemoReservationFactory {
    public DemoReservation createDemoReservation(String mobileNumber, Actor actor) {
        return new DemoReservation(mobileNumber, actor);
    }
}
