package com.company.andy.feature.maintenance.controller;

import com.company.andy.common.model.operator.Operator;
import com.company.andy.common.util.PagedResponse;
import com.company.andy.common.util.ResponseId;
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
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@Profile("local | it | it-local")
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
    public ResponseId createMaintenanceRecord(@RequestBody @Valid CreateMaintenanceRecordCommand command, @AuthenticationPrincipal Operator operator) {
        return new ResponseId(maintenanceRecordCommandService.createMaintenanceRecord(command, operator));
    }

    @Operation(summary = "Delete a maintenance record")
    @DeleteMapping("/{maintenanceRecordId}")
    public void deleteMaintenanceRecord(@PathVariable("maintenanceRecordId") @NotBlank String maintenanceRecordId, @AuthenticationPrincipal Operator operator) {
        this.maintenanceRecordCommandService.deleteMaintenanceRecord(maintenanceRecordId, operator);
    }

    @Operation(summary = "Query maintenance records")
    @PostMapping("/paged")
    public PagedResponse<QPagedMaintenanceRecord> pageMaintenanceRecords(@RequestBody @Valid PageMaintenanceRecordsQuery query, @AuthenticationPrincipal Operator operator) {
        return maintenanceRecordQueryService.pageMaintenanceRecords(query, operator);
    }

    @Operation(summary = "Get maintenance record detail")
    @GetMapping("/{maintenanceRecordId}")
    public QDetailedMaintenanceRecord getMaintenanceRecordDetail(@PathVariable("maintenanceRecordId") @NotBlank String maintenanceRecordId, @AuthenticationPrincipal Operator operator) {
        return maintenanceRecordQueryService.getMaintenanceRecordDetail(maintenanceRecordId, operator);
    }

}
