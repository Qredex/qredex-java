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

import com.qredex.QredexConfig;
import com.qredex.QredexLogger;
import com.qredex.exceptions.QredexAuthenticationException;
import com.qredex.exceptions.QredexAuthorizationException;
import com.qredex.exceptions.QredexNetworkException;
import com.qredex.model.response.OAuthTokenResponse;

/**
 * Manages client-credentials token lifecycle: fetching, caching, and reuse.
 *
 * <p>Token is cached in memory and reused until 30 seconds before expiry.
 * Thread-safe: concurrent callers share a single in-flight fetch.
 */
public final class TokenProvider {

    /** Refresh the token this many milliseconds before its actual expiry. */
    private static final long REFRESH_WINDOW_MS = 30_000L;

    private final QredexConfig config;
    private final HttpTransport transport;
    private final QredexLogger logger;

    private volatile CachedToken cached;
    private final Object lock = new Object();

    public TokenProvider(QredexConfig config, HttpTransport transport) {
        this.config = config;
        this.transport = transport;
        this.logger = config.getLogger();
    }

    /**
     * Returns a valid {@code Authorization} header value, fetching a new token if needed.
     * Token is reused for its full lifetime (minus the 30-second refresh window).
     */
    public String getAuthorizationHeader() {
        CachedToken token = cached;
        long now = System.currentTimeMillis();

        if (token != null && token.isValid(now, REFRESH_WINDOW_MS)) {
            if (logger != null) logger.debug("[qredex] auth: cache hit, expiresAt=" + token.expiresAtMs);
            return token.authorizationHeader();
        }

        synchronized (lock) {
            token = cached;
            now = System.currentTimeMillis();
            if (token != null && token.isValid(now, REFRESH_WINDOW_MS)) {
                return token.authorizationHeader();
            }
            if (logger != null) logger.debug("[qredex] auth: cache miss, fetching new token");
            token = fetchWithRetry();
            cached = token;
            return token.authorizationHeader();
        }
    }

    /**
     * Explicitly issues a new token and caches it.
     * Callers on the {@code auth()} surface call this.
     */
    public OAuthTokenResponse issueToken() {
        synchronized (lock) {
            OAuthTokenResponse response = transport.fetchToken(
                config.getClientId(), config.getClientSecret(), config.getScope());
            cached = new CachedToken(response, System.currentTimeMillis());
            if (logger != null) logger.info("[qredex] auth: token issued, scope=" + response.getScope());
            return response;
        }
    }

    /** Clears the in-memory token cache. The next request will fetch a fresh token. */
    public void clearTokenCache() {
        synchronized (lock) {
            cached = null;
            if (logger != null) logger.debug("[qredex] auth: token cache cleared");
        }
    }

    private CachedToken fetchWithRetry() {
        int maxAttempts = config.getMaxAuthRetries();
        Exception lastException = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                OAuthTokenResponse response = transport.fetchToken(
                    config.getClientId(), config.getClientSecret(), config.getScope());
                if (logger != null) logger.info("[qredex] auth: token issued, scope=" + response.getScope());
                return new CachedToken(response, System.currentTimeMillis());
            } catch (QredexAuthenticationException |
                     QredexAuthorizationException e) {
                // auth failures are not retryable
                throw e;
            } catch (Exception e) {
                lastException = e;
                if (attempt < maxAttempts) {
                    long delayMs = Math.min(500L * (1L << (attempt - 1)), 4000L);
                    if (logger != null) {
                        logger.warn("[qredex] auth: attempt " + attempt + " failed, retrying in " + delayMs + "ms");
                    }
                    try { Thread.sleep(delayMs); } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new QredexNetworkException("Token fetch interrupted.", ie);
                    }
                }
            }
        }
        if (lastException != null) {
            throw (RuntimeException) lastException;
        }
        throw new QredexNetworkException("Token issuance failed after " + maxAttempts + " attempts.", null);
    }
}
