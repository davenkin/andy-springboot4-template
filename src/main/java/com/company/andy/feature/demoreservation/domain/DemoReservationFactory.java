package com.company.andy.feature.demoreservation.domain;

import org.springframework.stereotype.Component;

// Always use factories to create AggregateRoot, as it makes the creation process of AggregateRoot more explicit.

@Component
public class DemoReservationFactory {
    public DemoReservation createDemoReservation(String mobileNumber) {
        return new DemoReservation(mobileNumber);
    }
}
