/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.model.standards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Signal type that triggered the duplicate detection. */
public enum DuplicateReason {
    EXACT_EXTERNAL_ORDER_ID("EXACT_EXTERNAL_ORDER_ID"),
    CHECKOUT_TOKEN_MATCH("CHECKOUT_TOKEN_MATCH"),
    CUSTOMER_EMAIL_HASH_MATCH("CUSTOMER_EMAIL_HASH_MATCH"),
    AMOUNT_TIME_MATCH("AMOUNT_TIME_MATCH"),

    /** Forward-compatible sentinel for values added after this SDK version. */
    UNKNOWN("UNKNOWN");

    private final String value;

    DuplicateReason(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static DuplicateReason fromValue(String value) {
        for (DuplicateReason s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        return UNKNOWN;
    }
}
