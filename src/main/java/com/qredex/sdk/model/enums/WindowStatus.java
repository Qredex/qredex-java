/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Whether an order falls within the creator's attribution window. */
public enum WindowStatus {
    WITHIN("WITHIN"),
    OUTSIDE("OUTSIDE"),
    UNKNOWN("UNKNOWN");

    private final String value;

    WindowStatus(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static WindowStatus fromValue(String value) {
        for (WindowStatus s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Unknown WindowStatus: " + value);
    }
}
