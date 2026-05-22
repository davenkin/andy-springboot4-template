package com.company.andy.feature.demo.controller;

import static com.company.andy.TestFixture.randomMobileNumber;

import com.company.andy.feature.demo.command.CreateDemoReservationCommand;

public class DemoReservationTestFixture {
  public static CreateDemoReservationCommand randomDemoReservationCommand() {
    return new CreateDemoReservationCommand(randomMobileNumber());
  }
}
