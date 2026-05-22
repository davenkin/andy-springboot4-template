package com.company.andy.feature.maintenance.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.company.andy.common.model.actor.OrgActor;
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
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseId createMaintenanceRecord(
      @RequestBody @Valid CreateMaintenanceRecordCommand command,
      @AuthenticationPrincipal OrgActor actor) {
    return new ResponseId(maintenanceRecordCommandService.createMaintenanceRecord(command, actor));
  }

  @Operation(summary = "Delete a maintenance record")
  @DeleteMapping("/{maintenanceRecordId}")
  public void deleteMaintenanceRecord(
      @PathVariable("maintenanceRecordId") @NotBlank String maintenanceRecordId,
      @AuthenticationPrincipal OrgActor actor) {
    this.maintenanceRecordCommandService.deleteMaintenanceRecord(maintenanceRecordId, actor);
  }

  @Operation(summary = "Query maintenance records")
  @PostMapping("/paged")
  public PagedResponse<QPagedMaintenanceRecord> pageMaintenanceRecords(
      @RequestBody @Valid PageMaintenanceRecordsQuery query,
      @AuthenticationPrincipal OrgActor actor) {
    return maintenanceRecordQueryService.pageMaintenanceRecords(query, actor);
  }

  @Operation(summary = "Get maintenance record detail")
  @GetMapping("/{maintenanceRecordId}")
  public QDetailedMaintenanceRecord getMaintenanceRecordDetail(
      @PathVariable("maintenanceRecordId") @NotBlank String maintenanceRecordId,
      @AuthenticationPrincipal OrgActor actor) {
    return maintenanceRecordQueryService.getMaintenanceRecordDetail(maintenanceRecordId, actor);
  }
}
