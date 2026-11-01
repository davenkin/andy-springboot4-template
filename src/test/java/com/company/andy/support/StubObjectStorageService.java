package com.company.andy.support;

import com.company.andy.common.storage.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("local-it-embedded")
@RequiredArgsConstructor
public class StubObjectStorageService implements ObjectStorageService {
}
