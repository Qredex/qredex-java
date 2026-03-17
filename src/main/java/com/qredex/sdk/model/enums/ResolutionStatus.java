/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Attribution resolution outcome for an order.
 *
 * <p>Do not collapse these into a boolean. Each value carries distinct platform semantics.
 */
public enum ResolutionStatus {
    ATTRIBUTED("ATTRIBUTED"),
    UNATTRIBUTED("UNATTRIBUTED"),
    REJECTED("REJECTED");

    private final String value;

    ResolutionStatus(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static ResolutionStatus fromValue(String value) {
        for (ResolutionStatus s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Unknown ResolutionStatus: " + value);
    }
}
