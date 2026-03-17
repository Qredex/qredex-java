/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Whether the PIT lock origin matches the link's expected domain. */
public enum OriginMatchStatus {
    MATCH("MATCH"),
    MISMATCH("MISMATCH"),
    ABSENT("ABSENT"),
    UNKNOWN("UNKNOWN");

    private final String value;

    OriginMatchStatus(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static OriginMatchStatus fromValue(String value) {
        for (OriginMatchStatus s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        return UNKNOWN;
    }
}
