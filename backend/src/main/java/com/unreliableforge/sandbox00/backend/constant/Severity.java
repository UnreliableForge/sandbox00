package com.unreliableforge.sandbox00.backend.constant;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * レスポンスの severity 定数定義。
 */
public enum Severity {
    SUCCESS("success"),
    INFO("info"),
    WARNING("warning"),
    ERROR("error");

    private final String label;

    Severity(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

}
