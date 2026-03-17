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
package com.qredex.model.standards;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Canonical OAuth scopes for the Qredex Integrations API.
 *
 * <p>Use these when explicitly requesting scopes during SDK initialization:
 * <pre>{@code
 * Qredex qredex = Qredex.builder()
 *     .clientId("...")
 *     .clientSecret("...")
 *     .scopes(QredexScope.LINKS_WRITE, QredexScope.INTENTS_WRITE, QredexScope.ORDERS_WRITE)
 *     .build();
 * }</pre>
 *
 * <p>When no scopes are specified, the server assigns the default scope for your credentials.
 *
 * <p>This enum covers Integrations API scopes only. Merchant and internal scopes
 * are not part of this SDK.
 */
public enum QredexScope {

    /** Full API access — equivalent to all read + write scopes. */
    API("direct:api"),

    /** Read-only access to influence links. */
    LINKS_READ("direct:links:read"),

    /** Create and manage influence links. */
    LINKS_WRITE("direct:links:write"),

    /** Read-only access to creators. */
    CREATORS_READ("direct:creators:read"),

    /** Create and manage creators. */
    CREATORS_WRITE("direct:creators:write"),

    /** Read-only access to order attributions. */
    ORDERS_READ("direct:orders:read"),

    /** Record paid orders and refunds, plus read access. */
    ORDERS_WRITE("direct:orders:write"),

    /** Read-only access to intent tokens. */
    INTENTS_READ("direct:intents:read"),

    /** Issue IIT and lock PIT tokens. */
    INTENTS_WRITE("direct:intents:write");

    private final String value;

    QredexScope(String value) { this.value = value; }

    /** The OAuth scope string sent to the token endpoint. */
    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static QredexScope fromValue(String value) {
        if (value == null) return null;
        for (QredexScope s : values()) {
            if (s.value.equalsIgnoreCase(value)) return s;
        }
        throw new IllegalArgumentException("Unknown QredexScope: " + value);
    }

    /**
     * Joins multiple scopes into a single space-delimited string for the OAuth token request.
     *
     * @param scopes one or more scopes
     * @return space-delimited scope string, or {@code null} if empty
     */
    public static String join(QredexScope... scopes) {
        if (scopes == null || scopes.length == 0) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < scopes.length; i++) {
            if (i > 0) sb.append(' ');
            sb.append(scopes[i].value);
        }
        return sb.toString();
    }
}
