package com.company.andy.feature.demoreservation;

import static com.company.andy.TestFixture.randomMobileNumber;

import com.company.andy.feature.demoreservation.command.CreateDemoReservationCommand;

public class DemoReservationTestFixture {
  public static CreateDemoReservationCommand randomDemoReservationCommand() {
    return new CreateDemoReservationCommand(randomMobileNumber());
  }
}
