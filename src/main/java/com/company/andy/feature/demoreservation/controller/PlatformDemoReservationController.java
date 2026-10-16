package com.company.andy.feature.demoreservation.controller;

import com.company.andy.common.utils.PagedResponse;
import com.company.andy.feature.demoreservation.query.DemoReservationQueryService;
import com.company.andy.feature.demoreservation.query.PageDemoReservationQuery;
import com.company.andy.feature.demoreservation.query.QPagedDemoReservation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Controller should be thin and calls into CommandService and QueryService.
// Controller should pass through the Actor to CommandService and QueryService.
// Here PlatformDemoReservationController belongs to the Platform API plane.


@Profile("local | it-embedded | it-local")
@Tag(name = "DemoReservationController", description = "Demo reservation APIs for Platform API plane")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/platform/demo-reservations")
public class PlatformDemoReservationController {
    private final DemoReservationQueryService demoReservationQueryService;

    @Operation(summary = "Query demo reservations")
    @PostMapping("/paged")
    public PagedResponse<QPagedDemoReservation> pageDemoReservations(@RequestBody @Valid PageDemoReservationQuery query) {
        return this.demoReservationQueryService.pageDemoReservations(query);
    }
}
