package com.company.andy.common.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Component
@Profile("!local-it-embedded")
@RequiredArgsConstructor
public class S3ObjectStorageService implements ObjectStorageService {
    private final S3Client s3Client;

}
