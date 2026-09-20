package com.company.andy.support;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.UUID;

public class TestIdExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        TestIdContext.setTestId(UUID.randomUUID().toString());
    }

    @Override
    public void afterEach(ExtensionContext context) {
        TestIdContext.clear();
    }
}