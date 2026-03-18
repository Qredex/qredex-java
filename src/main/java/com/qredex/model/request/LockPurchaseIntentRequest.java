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
package com.qredex.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qredex.exceptions.QredexValidationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Request body for locking a Purchase Intent Token (PIT) via the authenticated Integrations API.
 *
 * <p>This is the machine-to-machine PIT lock endpoint. It returns the full
 * {@code PurchaseIntentResponse} with complete attribution details and snapshots.
 *
 * <pre>{@code
 * LockPurchaseIntentRequest req = LockPurchaseIntentRequest.builder()
 *     .token("eyJhbGci...")
 *     .source("browser-cart")
 *     .build();
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class LockPurchaseIntentRequest {

    @JsonProperty("token")
    private final String token;

    @JsonProperty("source")
    private final String source;

    @JsonProperty("integrity_version")
    private final Integer integrityVersion;

    private LockPurchaseIntentRequest(Builder builder) {
        this.token = builder.token;
        this.source = builder.source;
        this.integrityVersion = builder.integrityVersion;
    }

    @NotNull
    public String getToken() { return token; }
    @Nullable
    public String getSource() { return source; }
    @Nullable
    public Integer getIntegrityVersion() { return integrityVersion; }

    @NotNull
    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String token;
        private String source;
        private Integer integrityVersion;

        private Builder() {}

        /** Required. The IIT token string to lock into a PIT. */
        @NotNull
        public Builder token(@NotNull String token) { this.token = token; return this; }

        /** Optional source label describing the locking context, e.g. {@code "browser-cart"}. */
        @NotNull
        public Builder source(@Nullable String source) { this.source = source; return this; }

        /** Optional integrity version used during token verification. */
        @NotNull
        public Builder integrityVersion(int integrityVersion) { this.integrityVersion = integrityVersion; return this; }

        @NotNull
        public LockPurchaseIntentRequest build() {
            if (token == null || token.trim().isEmpty())
                throw new QredexValidationException("LockPurchaseIntentRequest requires a token.");
            return new LockPurchaseIntentRequest(this);
        }
    }

    @Override
    public String toString() {
        return "LockPurchaseIntentRequest{source='" + source + "'}";
    }
}
