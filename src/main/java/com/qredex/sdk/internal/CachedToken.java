/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.internal;

import com.qredex.sdk.model.response.OAuthTokenResponse;

/** An in-memory cached access token. Thread-safe via volatile reads and synchronized writes. */
final class CachedToken {

    final String accessToken;
    final String tokenType;
    final long expiresAtMs;
    final String scope;

    CachedToken(OAuthTokenResponse response, long issuedAtMs) {
        this.accessToken = response.getAccessToken();
        this.tokenType = response.getTokenType() != null ? response.getTokenType() : "Bearer";
        this.expiresAtMs = issuedAtMs + (response.getExpiresIn() != null ? response.getExpiresIn() * 1000L : 3600_000L);
        this.scope = response.getScope();
    }

    boolean isValid(long nowMs, long refreshWindowMs) {
        return (expiresAtMs - refreshWindowMs) > nowMs;
    }

    String authorizationHeader() {
        return tokenType + " " + accessToken;
    }
}
