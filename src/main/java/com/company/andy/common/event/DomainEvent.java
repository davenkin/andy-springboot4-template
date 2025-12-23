package com.company.andy.common.event;

import static java.util.Objects.requireNonNull;

import static com.company.andy.common.util.SnowflakeIdGenerator.newSnowflakeId;
import static lombok.AccessLevel.PROTECTED;

import java.time.Instant;

import com.company.andy.common.model.AggregateRoot;
import com.company.andy.feature.equipment.domain.event.EquipmentCreatedEvent;
import com.company.andy.feature.equipment.domain.event.EquipmentDeletedEvent;
import com.company.andy.feature.equipment.domain.event.EquipmentNameUpdatedEvent;
import com.company.andy.feature.equipment.domain.event.EquipmentStatusUpdatedEvent;
import com.company.andy.feature.maintenance.domain.event.MaintenanceRecordCreatedEvent;
import com.company.andy.feature.maintenance.domain.event.MaintenanceRecordDeletedEvent;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true)
@JsonSubTypes(value = {
    @Type(value = EquipmentCreatedEvent.class, name = "EQUIPMENT_CREATED_EVENT"),
    @Type(value = EquipmentDeletedEvent.class, name = "EQUIPMENT_DELETED_EVENT"),
    @Type(value = EquipmentNameUpdatedEvent.class, name = "EQUIPMENT_NAME_UPDATED_EVENT"),
    @Type(value = EquipmentStatusUpdatedEvent.class, name = "EQUIPMENT_STATUS_UPDATED_EVENT"),
    @Type(value = MaintenanceRecordCreatedEvent.class, name = "MAINTENANCE_RECORD_CREATED_EVENT"),
    @Type(value = MaintenanceRecordDeletedEvent.class, name = "MAINTENANCE_RECORD_DELETED_EVENT"),
})

// Base class for all domain events
@Getter
@FieldNameConstants
@NoArgsConstructor(access = PROTECTED)
public abstract class DomainEvent {
  private String id;
  private String arId;
  private String arOrgId;
  private DomainEventType type;
  private Instant raisedAt;
  private String raisedBy;

  protected DomainEvent(DomainEventType type, AggregateRoot ar) {
    requireNonNull(type, "type must not be null.");
    requireNonNull(ar, "ar must not be null.");

    this.id = newEventId();
    this.arId = ar.getId();
    this.arOrgId = ar.getOrgId();
    this.type = type;
    this.raisedAt = Instant.now();
  }

  public static String newEventId() {
    return "EVT" + newSnowflakeId();
  }

  public void raisedBy(String raisedBy) {
    this.raisedBy = raisedBy;
  }
}
