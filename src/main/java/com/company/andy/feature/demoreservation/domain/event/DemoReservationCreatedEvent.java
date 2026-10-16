package com.company.andy.feature.demoreservation.domain.event;

import com.company.andy.common.event.DomainEvent;
import com.company.andy.feature.demoreservation.domain.DemoReservation;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.TypeAlias;

import static com.company.andy.common.event.DomainEventType.DEMO_RESERVATION_CREATED_EVENT;
import static lombok.AccessLevel.PRIVATE;

// All DomainEvents should extend from DomainEvent, directly or indirectly..

@Getter
@TypeAlias("DEMO_RESERVATION_CREATED_EVENT")
@NoArgsConstructor(access = PRIVATE, onConstructor_ = @JsonCreator)
public class DemoReservationCreatedEvent extends DomainEvent {
    private String reservationId;
    private String mobileNumber;

    public DemoReservationCreatedEvent(DemoReservation reservation) {
        super(DEMO_RESERVATION_CREATED_EVENT, reservation);
        this.reservationId = reservation.getId();
        this.mobileNumber = reservation.getMobileNumber();
    }
}
