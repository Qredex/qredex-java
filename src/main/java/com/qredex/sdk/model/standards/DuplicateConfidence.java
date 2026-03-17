/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.model.standards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Confidence level that an order is a duplicate of a previously recorded order. */
public enum DuplicateConfidence {
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH"),

    /** Forward-compatible sentinel for values added after this SDK version. */
    UNKNOWN("UNKNOWN");

    private final String value;

    DuplicateConfidence(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static DuplicateConfidence fromValue(String value) {
        for (DuplicateConfidence s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        return UNKNOWN;
    }
}
