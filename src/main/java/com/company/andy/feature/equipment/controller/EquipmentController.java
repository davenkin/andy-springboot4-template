package com.company.andy.feature.equipment.controller;

import com.company.andy.common.model.actor.OrgActor;
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
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@Profile("local | it | it-local")
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
    public ResponseId createEquipment(@RequestBody @Valid CreateEquipmentCommand command, @AuthenticationPrincipal OrgActor actor) {
        return new ResponseId(this.equipmentCommandService.createEquipment(command, actor));
    }

    @Operation(summary = "Update an equipment's name")
    @PutMapping("/{equipmentId}/name")
    public void updateEquipmentName(
            @PathVariable("equipmentId") @NotBlank
            @Parameter(description = "Id of the equipment")
            String equipmentId,
            @RequestBody @Valid UpdateEquipmentNameCommand command,
            @AuthenticationPrincipal OrgActor actor) {
        this.equipmentCommandService.updateEquipmentName(equipmentId, command, actor);
    }

    @Operation(summary = "Update an equipment's holder")
    @PutMapping("/{equipmentId}/holder")
    public void updateEquipmentHolder(
            @PathVariable("equipmentId") @NotBlank String equipmentId,
            @RequestBody @Valid UpdateEquipmentHolderCommand command,
            @AuthenticationPrincipal OrgActor actor) {
        this.equipmentCommandService.updateEquipmentHolder(equipmentId, command, actor);
    }

    @Operation(summary = "Delete an equipment")
    @DeleteMapping("/{equipmentId}")
    public void deleteEquipment(@PathVariable("equipmentId") @NotBlank String equipmentId, @AuthenticationPrincipal OrgActor actor) {
        this.equipmentCommandService.deleteEquipment(equipmentId, actor);
    }

    @Operation(summary = "Query equipments")
    @PostMapping("/paged")
    public PagedResponse<QPagedEquipment> pageEquipments(
            @RequestBody @Valid PageEquipmentsQuery query,
            @AuthenticationPrincipal OrgActor actor) {
        return this.equipmentQueryService.pageEquipments(query, actor);
    }

    @Operation(summary = "Get equipment detail")
    @GetMapping("/{equipmentId}")
    public QDetailedEquipment getEquipmentDetail(
            @PathVariable("equipmentId") @NotBlank String equipmentId,
            @AuthenticationPrincipal OrgActor actor) {
        return this.equipmentQueryService.getEquipmentDetail(equipmentId, actor);
    }

    @Operation(summary = "Get all equipment summaries for an organization")
    @GetMapping("/summaries")
    public List<EquipmentSummary> getAllEquipmentSummaries(@AuthenticationPrincipal OrgActor actor) {
        return this.equipmentQueryService.getAllEquipmentSummaries(actor);
    }
}
