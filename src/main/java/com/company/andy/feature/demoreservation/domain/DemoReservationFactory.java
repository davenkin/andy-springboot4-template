package com.company.andy.feature.demoreservation.domain;

import com.company.andy.common.model.actor.AnonymousActor;
import org.springframework.stereotype.Component;

@Component
public class DemoReservationFactory {
  public DemoReservation createDemoReservation(String mobileNumber, AnonymousActor actor) {
    return new DemoReservation(mobileNumber, actor);
  }
}
