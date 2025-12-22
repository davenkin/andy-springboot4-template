package com.company.andy.business.equipment.query;

import com.company.andy.business.equipment.domain.EquipmentStatus;
import com.company.andy.common.util.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static lombok.AccessLevel.PRIVATE;

@Getter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = PRIVATE)
public class PageEquipmentsQuery extends PageQuery {
    @Schema(description = "Search text")
    @Max(50)
    private String search;

    @Schema(description = "Equipment status to query")
    private EquipmentStatus status;
}

