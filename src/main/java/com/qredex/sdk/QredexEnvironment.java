/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk;

/**
 * Qredex API environment. Controls the base URL used by the SDK.
 */
public enum QredexEnvironment {

    /** Live production API at {@code https://api.qredex.com}. */
    PRODUCTION("https://api.qredex.com"),

    /** Staging environment at {@code https://staging-api.qredex.com}. */
    STAGING("https://staging-api.qredex.com"),

    /** Local development server at {@code http://localhost:8080}. */
    DEVELOPMENT("http://localhost:8080");

    private final String defaultBaseUrl;

    QredexEnvironment(String defaultBaseUrl) {
        this.defaultBaseUrl = defaultBaseUrl;
    }

    /** The default base URL for this environment. */
    public String getDefaultBaseUrl() { return defaultBaseUrl; }

    /** Resolves from the string values accepted in the {@code QREDEX_ENVIRONMENT} env var. */
    public static QredexEnvironment fromString(String value) {
        if (value == null) return PRODUCTION;
        switch (value.toLowerCase()) {
            case "production": return PRODUCTION;
            case "staging":    return STAGING;
            case "development":
            case "dev":        return DEVELOPMENT;
            default:
                throw new IllegalArgumentException(
                    "Unknown Qredex environment: '" + value +
                    "'. Use 'production', 'staging', or 'development'.");
        }
    }
}
