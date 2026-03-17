/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Integrity verdict for a Purchase Intent Token (PIT). */
public enum TokenIntegrity {
    VALID("VALID"),
    INVALID("INVALID"),

    /** Forward-compatible sentinel for values added after this SDK version. */
    UNKNOWN("UNKNOWN");

    private final String value;

    TokenIntegrity(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static TokenIntegrity fromValue(String value) {
        for (TokenIntegrity s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        return UNKNOWN;
    }
}
