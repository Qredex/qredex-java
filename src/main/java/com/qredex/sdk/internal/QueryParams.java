/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * Internal helper that builds typed query-parameter maps from optional request fields.
 * Null values are omitted so the server receives only provided parameters.
 */
public final class QueryParams {

    private final Map<String, String> params = new HashMap<>();

    public QueryParams add(String key, Object value) {
        if (value != null) params.put(key, String.valueOf(value));
        return this;
    }

    public Map<String, String> build() { return params; }

    private QueryParams() {}

    public static QueryParams create() { return new QueryParams(); }
}
