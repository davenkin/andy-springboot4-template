package com.company.andy.feature.demo.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.company.andy.common.model.actor.AnonymousActor;
import com.company.andy.common.utils.ResponseId;
import com.company.andy.feature.demo.command.CreateDemoReservationCommand;
import com.company.andy.feature.demo.command.DemoReservationCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Profile("local | it | it-local")
@Tag(name = "DemoReservationController", description = "Demo reservation APIs")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "system/demo-reservations")
public class DemoReservationController {
  private final DemoReservationCommandService demoReservationCommandService;

  @PostMapping
  @ResponseStatus(CREATED)
  @Operation(summary = "Create a demo reservation")
  public ResponseId createDemoReservation(
      @RequestBody @Valid CreateDemoReservationCommand command,
      @AuthenticationPrincipal AnonymousActor actor) {
    return new ResponseId(this.demoReservationCommandService.createDemoReservation(command, actor));
  }
}
