/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.model.standards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Integrity confidence band for an order attribution. */
public enum IntegrityBand {
    HIGH("HIGH"),
    MEDIUM("MEDIUM"),
    LOW("LOW"),
    CRITICAL("CRITICAL"),

    /** Forward-compatible sentinel for values added after this SDK version. */
    UNKNOWN("UNKNOWN");

    private final String value;

    IntegrityBand(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static IntegrityBand fromValue(String value) {
        for (IntegrityBand s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        return UNKNOWN;
    }
}
