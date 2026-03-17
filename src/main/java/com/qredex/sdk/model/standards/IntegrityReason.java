/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.model.standards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Reason a Purchase Intent Token (PIT) failed integrity verification. */
public enum IntegrityReason {
    MISSING("MISSING"),
    TAMPERED("TAMPERED"),
    EXPIRED("EXPIRED"),
    MISMATCHED("MISMATCHED"),
    REPLACED("REPLACED"),
    LINK_INACTIVE("LINK_INACTIVE"),
    CREATOR_INACTIVE("CREATOR_INACTIVE"),

    /** Forward-compatible sentinel for values added after this SDK version. */
    UNKNOWN("UNKNOWN");

    private final String value;

    IntegrityReason(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static IntegrityReason fromValue(String value) {
        for (IntegrityReason s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        return UNKNOWN;
    }
}
