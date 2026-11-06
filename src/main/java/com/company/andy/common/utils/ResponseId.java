package com.company.andy.common.utils;

import static com.company.andy.common.utils.CommonUtils.requireNonBlank;

public record ResponseId(String id) {
    public ResponseId {
        requireNonBlank(id, "ResponseId cannot be null.");
    }
}
