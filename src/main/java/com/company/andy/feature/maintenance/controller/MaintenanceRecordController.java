package com.company.andy.feature.maintenance.controller;

import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.utils.PagedResponse;
import com.company.andy.common.utils.ResponseId;
import com.company.andy.feature.maintenance.command.CreateMaintenanceRecordCommand;
import com.company.andy.feature.maintenance.command.MaintenanceRecordCommandService;
import com.company.andy.feature.maintenance.query.MaintenanceRecordQueryService;
import com.company.andy.feature.maintenance.query.PageMaintenanceRecordsQuery;
import com.company.andy.feature.maintenance.query.QDetailedMaintenanceRecord;
import com.company.andy.feature.maintenance.query.QPagedMaintenanceRecord;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

// Controller should be thin and calls into CommandService and QueryService.

@Profile("local | it-embedded | it-local")
@Tag(name = "MaintenanceRecordController", description = "Equipments' maintenance record APIs")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/maintenance-records")
public class MaintenanceRecordController {
    private final MaintenanceRecordCommandService maintenanceRecordCommandService;
    private final MaintenanceRecordQueryService maintenanceRecordQueryService;

    @Operation(summary = "Create a maintenance record")
    @ResponseStatus(CREATED)
    @PostMapping
    public ResponseId createMaintenanceRecord(
            @RequestBody @Valid CreateMaintenanceRecordCommand command,
            @AuthenticationPrincipal @NotNull Actor actor) {
        return new ResponseId(maintenanceRecordCommandService.createMaintenanceRecord(command, actor.requireOrgId()));
    }

    @Operation(summary = "Delete a maintenance record")
    @DeleteMapping("/{id}")
    public void deleteMaintenanceRecord(
            @PathVariable("id") @NotBlank String maintenanceRecordId,
            @AuthenticationPrincipal @NotNull Actor actor) {
        this.maintenanceRecordCommandService.deleteMaintenanceRecord(maintenanceRecordId, actor.requireOrgId());
    }

    @Operation(summary = "Query maintenance records with pagination")
    @PostMapping("/paged")
    public PagedResponse<QPagedMaintenanceRecord> pageMaintenanceRecords(
            @RequestBody @Valid PageMaintenanceRecordsQuery query,
            @AuthenticationPrincipal @NotNull Actor actor) {
        return maintenanceRecordQueryService.pageMaintenanceRecords(query, actor.requireOrgId());
    }

    @Operation(summary = "Get a maintenance record's detail")
    @GetMapping("/{id}")
    public QDetailedMaintenanceRecord getMaintenanceRecordDetail(
            @PathVariable("id") @NotBlank String maintenanceRecordId,
            @AuthenticationPrincipal @NotNull Actor actor) {
        return maintenanceRecordQueryService.getMaintenanceRecordDetail(maintenanceRecordId, actor.requireOrgId());
    }
}
