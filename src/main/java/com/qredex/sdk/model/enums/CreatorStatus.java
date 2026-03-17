/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Status of a Qredex creator account. */
public enum CreatorStatus {
    ACTIVE("ACTIVE"),
    DISABLED("DISABLED");

    private final String value;

    CreatorStatus(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static CreatorStatus fromValue(String value) {
        for (CreatorStatus s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Unknown CreatorStatus: " + value);
    }
}
