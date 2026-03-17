/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.internal;

/** Produces the SDK User-Agent header value. */
public final class QredexUserAgent {

    static final String SDK_VERSION = "1.0.0";
    private static final String JAVA_VERSION = System.getProperty("java.version", "unknown");

    public static String build(String suffix) {
        String base = "qredex-java/" + SDK_VERSION + " Java/" + JAVA_VERSION;
        if (suffix != null && !suffix.trim().isEmpty()) {
            return base + " " + suffix.trim();
        }
        return base;
    }

    private QredexUserAgent() {}
}
