package com.company.andy.feature.demoreservation.controller;

import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.utils.PagedResponse;
import com.company.andy.common.utils.ResponseId;
import com.company.andy.feature.demoreservation.command.CreateDemoReservationCommand;
import com.company.andy.feature.demoreservation.command.DemoReservationCommandService;
import com.company.andy.feature.demoreservation.query.DemoReservationQueryService;
import com.company.andy.feature.demoreservation.query.PageDemoReservationQuery;
import com.company.andy.feature.demoreservation.query.QPagedDemoReservation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@Profile("local | it | it-local")
@Tag(name = "DemoReservationController", description = "Demo reservation APIs")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "system/demo-reservations")
public class DemoReservationController {
    private final DemoReservationCommandService demoReservationCommandService;
    private final DemoReservationQueryService demoReservationQueryService;

    // This API is open for anyone, including anonymous users
    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(summary = "Create a demo reservation")
    public ResponseId createDemoReservation(
            @RequestBody @Valid CreateDemoReservationCommand command,
            @AuthenticationPrincipal Actor actor) {
        return new ResponseId(this.demoReservationCommandService.createDemoReservation(command, actor));
    }

    @Operation(summary = "Query demo reservations")
    @PostMapping("/paged")
    public PagedResponse<QPagedDemoReservation> pageDemoReservations(@RequestBody @Valid PageDemoReservationQuery query) {
        return this.demoReservationQueryService.pageDemoReservations(query);
    }
}
