package com.company.andy.feature.demoreservation.controller;

import com.company.andy.common.utils.ResponseId;
import com.company.andy.feature.demoreservation.command.CreateDemoReservationCommand;
import com.company.andy.feature.demoreservation.command.DemoReservationCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

// Controller should be thin and calls into CommandService and QueryService.
// Controller should pass through the Actor to CommandService and QueryService.

@Profile("local | it-embedded | it-local")
@Tag(name = "DemoReservationController", description = "Demo reservation APIs")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/demo-reservations")
public class DemoReservationController {
    private final DemoReservationCommandService demoReservationCommandService;

    // This API is for anonymous access
    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(summary = "Create a demo reservation")
    public ResponseId createDemoReservation(
            @RequestBody @Valid CreateDemoReservationCommand command) {
        return new ResponseId(this.demoReservationCommandService.createDemoReservation(command));
    }

}
