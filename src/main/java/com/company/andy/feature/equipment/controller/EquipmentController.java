package com.company.andy.feature.equipment.controller;

import com.company.andy.common.model.operator.Operator;
import com.company.andy.common.model.operator.UserOperator;
import com.company.andy.common.util.PagedResponse;
import com.company.andy.common.util.ResponseId;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.company.andy.common.model.Role.ORG_ADMIN;
import static org.springframework.http.HttpStatus.CREATED;

@Profile("local | it | it-local")
@Tag(name = "EquipmentController", description = "Equipment management APIs")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/equipments")
public class EquipmentController {
    private static final Operator SAMPLE_USER_OPERATOR = UserOperator.of("sampleUserId", "sampleUserName", ORG_ADMIN, "sampleOrgId");
    private final EquipmentCommandService equipmentCommandService;
    private final EquipmentQueryService equipmentQueryService;

    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(summary = "Create an equipment")
    public ResponseId createEquipment(@RequestBody @Valid CreateEquipmentCommand command) {
        // In real situations, operator is normally created from the current user in context, such as Spring Security's SecurityContextHolder
        Operator operator = SAMPLE_USER_OPERATOR;

        return new ResponseId(this.equipmentCommandService.createEquipment(command, operator));
    }

    @Operation(summary = "Update an equipment's name")
    @PutMapping("/{equipmentId}/name")
    public void updateEquipmentName(@PathVariable("equipmentId") @NotBlank
                                    @Parameter(description = "Id of the equipment")
                                    String equipmentId,
                                    @RequestBody @Valid UpdateEquipmentNameCommand updateEquipmentNameCommand) {
        // In real situations, operator is normally created from the current user in context, such as Spring Security's SecurityContextHolder
        Operator operator = SAMPLE_USER_OPERATOR;

        this.equipmentCommandService.updateEquipmentName(equipmentId, updateEquipmentNameCommand, operator);
    }

    @Operation(summary = "Update an equipment's holder")
    @PutMapping("/{equipmentId}/holder")
    public void updateEquipmentHolder(@PathVariable("equipmentId") @NotBlank String equipmentId,
                                      @RequestBody @Valid UpdateEquipmentHolderCommand command) {
        // In real situations, operator is normally created from the current user in context, such as Spring Security's SecurityContextHolder
        Operator operator = SAMPLE_USER_OPERATOR;

        this.equipmentCommandService.updateEquipmentHolder(equipmentId, command, operator);
    }

    @Operation(summary = "Delete an equipment")
    @DeleteMapping("/{equipmentId}")
    public void deleteEquipment(@PathVariable("equipmentId") @NotBlank String equipmentId) {
        // In real situations, operator is normally created from the current user in context, such as Spring Security's SecurityContextHolder
        Operator operator = SAMPLE_USER_OPERATOR;

        this.equipmentCommandService.deleteEquipment(equipmentId, operator);
    }

    @Operation(summary = "Query equipments")
    @PostMapping("/paged")
    public PagedResponse<QPagedEquipment> pageEquipments(@RequestBody @Valid PageEquipmentsQuery query) {
        // In real situations, operator is normally created from the current user in context, such as Spring Security's SecurityContextHolder
        Operator operator = SAMPLE_USER_OPERATOR;

        return this.equipmentQueryService.pageEquipments(query, operator);
    }

    @Operation(summary = "Get equipment detail")
    @GetMapping("/{equipmentId}")
    public QDetailedEquipment getEquipmentDetail(@PathVariable("equipmentId") @NotBlank String equipmentId) {
        // In real situations, operator is normally created from the current user in context, such as Spring Security's SecurityContextHolder
        Operator operator = SAMPLE_USER_OPERATOR;

        return this.equipmentQueryService.getEquipmentDetail(equipmentId, operator);
    }

    @Operation(summary = "Get all equipment summaries for an organization")
    @GetMapping("/summaries")
    public List<EquipmentSummary> getAllEquipmentSummaries() {
        // In real situations, operator is normally created from the current user in context, such as Spring Security's SecurityContextHolder
        Operator operator = SAMPLE_USER_OPERATOR;

        return this.equipmentQueryService.getAllEquipmentSummaries(operator);
    }

}
