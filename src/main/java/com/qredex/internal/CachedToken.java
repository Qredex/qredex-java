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
package com.qredex.internal;

import com.qredex.model.response.OAuthTokenResponse;

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
