package com.company.andy.feature.org.equipment.query;

import static lombok.AccessLevel.PRIVATE;

import com.company.andy.common.utils.PageQuery;
import com.company.andy.feature.org.equipment.domain.EquipmentStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

// All pagination queries should extends from PageQuery

@Getter
@SuperBuilder
@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator)
public class PageEquipmentsQuery extends PageQuery {
  @Schema(description = "Search text")
  @Max(50)
  private String search;

  @Schema(description = "Equipment status to query")
  private EquipmentStatus status;
}

