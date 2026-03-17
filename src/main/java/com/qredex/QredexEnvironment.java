/*
 *    ▄▄▄▄
 *  ▄█▀▀███▄▄              █▄
 *  ██    ██ ▄             ██
 *  ██    ██ ████▄▄█▀█▄ ▄████ ▄█▀█▄▀██ ██▀
 *  ██  ▄ ██ ██   ██▄█▀ ██ ██ ██▄█▀  ███
 *   ▀█████▄▄█▀  ▄▀█▄▄▄▄█▀███▄▀█▄▄▄▄██ ██▄
 *        ▀█
 *
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  Licensed under the Apache License, Version 2.0. See LICENSE for the full license text.
 *  You may not use this file except in compliance with that License.
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 *  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific language governing permissions
 *  and limitations under the License.
 *
 *  If you need additional information or have any questions, please email: copyright@qredex.com
 */
package com.qredex;

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
