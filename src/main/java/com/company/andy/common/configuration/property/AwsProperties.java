package com.company.andy.common.configuration.property;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("aws")
public record AwsProperties(@Valid @NotNull S3 s3) {
    public record S3(@NotBlank String region,
                     @NotBlank String bucket) {
    }
}