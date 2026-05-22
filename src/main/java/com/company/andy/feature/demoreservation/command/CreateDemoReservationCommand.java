package com.company.andy.feature.demoreservation.command;

import com.company.andy.common.validation.mobilenumber.MobileNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateDemoReservationCommand(
    @Schema(description = "Contact mobile number")
    @NotBlank @MobileNumber String mobileNumber) {
}
