package com.company.andy.feature.equipment.controller;

import com.company.andy.common.model.actor.Actor;
import com.company.andy.common.utils.PagedResponse;
import com.company.andy.common.utils.ResponseId;
import com.company.andy.feature.equipment.command.CreateEquipmentCommand;
import com.company.andy.feature.equipment.command.EquipmentCommandService;
import com.company.andy.feature.equipment.command.UpdateEquipmentHolderCommand;
import com.company.andy.feature.equipment.command.UpdateEquipmentNameCommand;
import com.company.andy.feature.equipment.domain.EquipmentSummary;
import com.company.andy.feature.equipment.query.EquipmentQueryService;
import com.company.andy.feature.equipment.query.PageEquipmentsQuery;
import com.company.andy.feature.equipment.query.QDetailedEquipment;
import com.company.andy.feature.equipment.query.QPagedEquipment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

// Controller should be thin and calls into CommandService and QueryService.
// Controller should pass through the Actor to CommandService and QueryService.
// Actor should be either OrgActor or PlatformActor according to which API plane you are handling.
// Here EquipmentController belongs to the Org API plane so OrgActor should be used.

@Profile("local | it-embedded | it-local")
@Tag(name = "EquipmentController", description = "Equipment management APIs")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/equipments")
public class EquipmentController {
    private final EquipmentCommandService equipmentCommandService;
    private final EquipmentQueryService equipmentQueryService;

    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(summary = "Create an equipment")
    public ResponseId createEquipment(@RequestBody @Valid CreateEquipmentCommand command,
                                      @AuthenticationPrincipal @NotNull Actor actor) {
        return new ResponseId(this.equipmentCommandService.createEquipment(command, actor.requireOrgId()));
    }

    @Operation(summary = "Update an equipment's name")
    @PutMapping("/{id}/name")
    public void updateEquipmentName(
            @PathVariable("id") @NotBlank
            @Parameter(description = "Id of the equipment")
            String equipmentId,
            @RequestBody @Valid UpdateEquipmentNameCommand command,
            @AuthenticationPrincipal @NotNull Actor actor) {
        this.equipmentCommandService.updateEquipmentName(equipmentId, command, actor.requireOrgId());
    }

    @Operation(summary = "Update an equipment's holder")
    @PutMapping("/{id}/holder")
    public void updateEquipmentHolder(
            @PathVariable("id") @NotBlank String equipmentId,
            @RequestBody @Valid UpdateEquipmentHolderCommand command,
            @AuthenticationPrincipal @NotNull Actor actor) {
        this.equipmentCommandService.updateEquipmentHolder(equipmentId, command, actor.requireOrgId());
    }

    @Operation(summary = "Delete an equipment")
    @DeleteMapping("/{id}")
    public void deleteEquipment(@PathVariable("id") @NotBlank String equipmentId,
                                @AuthenticationPrincipal @NotNull Actor actor) {
        this.equipmentCommandService.deleteEquipment(equipmentId, actor.requireOrgId());
    }

    @Operation(summary = "Query equipments with pagination")
    @PostMapping("/paged")
    public PagedResponse<QPagedEquipment> pageEquipments(
            @RequestBody @Valid PageEquipmentsQuery query,
            @AuthenticationPrincipal @NotNull Actor actor) {
        return this.equipmentQueryService.pageEquipments(query, actor.requireOrgId());
    }

    @Operation(summary = "Get an equipment's detail")
    @GetMapping("/{id}")
    public QDetailedEquipment getEquipmentDetail(
            @PathVariable("id") @NotBlank String equipmentId,
            @AuthenticationPrincipal @NotNull Actor actor) {
        return this.equipmentQueryService.getEquipmentDetail(equipmentId, actor.requireOrgId());
    }

    @Operation(summary = "Get all equipment summaries for an organization")
    @GetMapping("/summaries")
    public List<EquipmentSummary> getAllEquipmentSummaries(@AuthenticationPrincipal @NotNull Actor actor) {
        return this.equipmentQueryService.getAllEquipmentSummaries(actor.requireOrgId());
    }
}
