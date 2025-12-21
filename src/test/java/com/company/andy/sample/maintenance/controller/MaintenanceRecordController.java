package com.company.andy.sample.maintenance.controller;

import com.company.andy.common.model.operator.Operator;
import com.company.andy.common.util.PagedResponse;
import com.company.andy.common.util.ResponseId;
import com.company.andy.sample.maintenance.command.CreateMaintenanceRecordCommand;
import com.company.andy.sample.maintenance.command.MaintenanceRecordCommandService;
import com.company.andy.sample.maintenance.query.PageMaintenanceRecordsQuery;
import com.company.andy.sample.maintenance.query.MaintenanceRecordQueryService;
import com.company.andy.sample.maintenance.query.QDetailedMaintenanceRecord;
import com.company.andy.sample.maintenance.query.QPagedMaintenanceRecord;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.company.andy.sample.SampleFixture.SAMPLE_USER_OPERATOR;
import static org.springframework.http.HttpStatus.CREATED;

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
    public ResponseId createMaintenanceRecord(@RequestBody @Valid CreateMaintenanceRecordCommand command) {
        // In real situations, operator is normally created from the current user in context, such as Spring Security's SecurityContextHolder
        Operator operator = SAMPLE_USER_OPERATOR;

        return new ResponseId(maintenanceRecordCommandService.createMaintenanceRecord(command, operator));
    }

    @Operation(summary = "Delete a maintenance record")
    @DeleteMapping("/{maintenanceRecordId}")
    public void deleteMaintenanceRecord(@PathVariable("maintenanceRecordId") @NotBlank String maintenanceRecordId) {
        // In real situations, operator is normally created from the current user in context, such as Spring Security's SecurityContextHolder
        Operator operator = SAMPLE_USER_OPERATOR;

        this.maintenanceRecordCommandService.deleteMaintenanceRecord(maintenanceRecordId, operator);
    }

    @Operation(summary = "Query maintenance records")
    @PostMapping("/paged")
    public PagedResponse<QPagedMaintenanceRecord> pageMaintenanceRecords(@RequestBody @Valid PageMaintenanceRecordsQuery query) {
        // In real situations, operator is normally created from the current user in context, such as Spring Security's SecurityContextHolder
        Operator operator = SAMPLE_USER_OPERATOR;

        return maintenanceRecordQueryService.pageMaintenanceRecords(query, operator);
    }

    @Operation(summary = "Get maintenance record detail")
    @GetMapping("/{maintenanceRecordId}")
    public QDetailedMaintenanceRecord getMaintenanceRecordDetail(@PathVariable("maintenanceRecordId") @NotBlank String maintenanceRecordId) {
        // In real situations, operator is normally created from the current user in context, such as Spring Security's SecurityContextHolder
        Operator operator = SAMPLE_USER_OPERATOR;

        return maintenanceRecordQueryService.getMaintenanceRecordDetail(maintenanceRecordId, operator);
    }

}
