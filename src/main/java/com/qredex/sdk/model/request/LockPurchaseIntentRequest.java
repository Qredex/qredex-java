/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    public String getToken() { return token; }
    public String getSource() { return source; }
    public Integer getIntegrityVersion() { return integrityVersion; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String token;
        private String source;
        private Integer integrityVersion;

        private Builder() {}

        /** Required. The IIT token string to lock into a PIT. */
        public Builder token(String token) { this.token = token; return this; }

        /** Optional source label describing the locking context, e.g. {@code "browser-cart"}. */
        public Builder source(String source) { this.source = source; return this; }

        /** Optional integrity version used during token verification. */
        public Builder integrityVersion(int integrityVersion) { this.integrityVersion = integrityVersion; return this; }

        public LockPurchaseIntentRequest build() {
            if (token == null || token.trim().isEmpty())
                throw new IllegalStateException("LockPurchaseIntentRequest requires a token.");
            return new LockPurchaseIntentRequest(this);
        }
    }
}
