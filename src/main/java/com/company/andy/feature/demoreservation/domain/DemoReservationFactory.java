package com.company.andy.feature.demoreservation.domain;

import com.company.andy.common.model.actor.Actor;
import org.springframework.stereotype.Component;

// Always use factories to create Aggregate Root objects,
// which makes the creation process of Aggregate Roots more explicit

@Component
public class DemoReservationFactory {
    public DemoReservation createDemoReservation(String mobileNumber, Actor actor) {
        return new DemoReservation(mobileNumber, actor);
    }
}
