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
package com.qredex.sdk;

import com.qredex.sdk.exceptions.QredexConfigurationException;
import com.qredex.sdk.internal.HttpTransport;
import com.qredex.sdk.internal.TokenProvider;
import com.qredex.sdk.model.response.OAuthTokenResponse;
import com.qredex.sdk.resources.CreatorsClient;
import com.qredex.sdk.resources.IntentsClient;
import com.qredex.sdk.resources.LinksClient;
import com.qredex.sdk.resources.OrdersClient;
import com.qredex.sdk.resources.RefundsClient;

/**
 * Main entrypoint for the Qredex Java server SDK.
 *
 * <p>Use the builder for explicit configuration:
 * <pre>{@code
 * Qredex qredex = Qredex.builder()
 *     .clientId(System.getenv("QREDEX_CLIENT_ID"))
 *     .clientSecret(System.getenv("QREDEX_CLIENT_SECRET"))
 *     .environment(QredexEnvironment.PRODUCTION)
 *     .build();
 * }</pre>
 *
 * <p>Or bootstrap from environment variables:
 * <pre>{@code
 * // Reads QREDEX_CLIENT_ID, QREDEX_CLIENT_SECRET, and optionally QREDEX_ENVIRONMENT
 * Qredex qredex = Qredex.bootstrap();
 * }</pre>
 *
 * <p>Resource accessors:
 * <pre>{@code
 * qredex.creators().create(...)
 * qredex.links().create(...)
 * qredex.intents().issueInfluenceIntentToken(...)
 * qredex.intents().lockPurchaseIntent(...)
 * qredex.orders().recordPaidOrder(...)
 * qredex.orders().list(...)
 * qredex.orders().getDetails(...)
 * qredex.refunds().recordRefund(...)
 * }</pre>
 *
 * <p>Auth is managed automatically. Tokens are cached in memory and reused until near expiry.
 * Use {@link #auth()} for explicit token control.
 *
 * <p>This SDK covers the <strong>Integrations API only</strong>. It does not include
 * the Merchant dashboard API, internal admin API, or browser agent behavior.
 */
public final class Qredex {

    private final QredexConfig config;

    private final QredexAuthClient auth;
    private final CreatorsClient creators;
    private final LinksClient links;
    private final IntentsClient intents;
    private final OrdersClient orders;
    private final RefundsClient refunds;

    private Qredex(QredexConfig config) {
        this.config = config;
        HttpTransport transport = new HttpTransport(config);
        TokenProvider tokenProvider = new TokenProvider(config, transport);

        this.auth = new QredexAuthClient(tokenProvider);
        this.creators = new CreatorsClient(transport, tokenProvider);
        this.links = new LinksClient(transport, tokenProvider);
        this.intents = new IntentsClient(transport, tokenProvider);
        this.orders = new OrdersClient(transport, tokenProvider);
        this.refunds = new RefundsClient(transport, tokenProvider);
    }

    // -------------------------------------------------------------------------
    // Static factory methods
    // -------------------------------------------------------------------------

    /**
     * Creates a client from an explicit {@link QredexConfig}.
     *
     * <p>This is the canonical explicit-configuration path:
     *
     * <pre>{@code
     * Qredex qredex = Qredex.init(
     *     QredexConfig.builder()
     *         .clientId("...")
     *         .clientSecret("...")
     *         .build()
     * );
     * }</pre>
     *
     * @param config a fully built {@link QredexConfig}
     * @return a configured {@link Qredex} client
     * @throws QredexConfigurationException if the config is invalid
     */
    public static Qredex init(QredexConfig config) {
        if (config == null) {
            throw new QredexConfigurationException("QredexConfig must not be null.");
        }
        return new Qredex(config);
    }

    /**
     * Creates a {@link Builder} for explicit SDK configuration.
     *
     * <p>Convenience alternative to {@link #init(QredexConfig)} — delegates to
     * {@link QredexConfig.Builder} and produces the same result:
     *
     * <pre>{@code
     * Qredex qredex = Qredex.builder()
     *     .clientId("...")
     *     .clientSecret("...")
     *     .build();
     * }</pre>
     */
    public static Builder builder() { return new Builder(); }

    /**
     * Bootstraps the client from environment variables.
     *
     * <p>Reads:
     * <ul>
     *   <li>{@code QREDEX_CLIENT_ID} (required)</li>
     *   <li>{@code QREDEX_CLIENT_SECRET} (required)</li>
     *   <li>{@code QREDEX_ENVIRONMENT} — {@code production}, {@code staging}, or {@code development} (optional, defaults to {@code production})</li>
     *   <li>{@code QREDEX_SCOPE} — space-separated OAuth scope string (optional)</li>
     * </ul>
     *
     * <p>Delegates to {@link #init(QredexConfig)} after resolving and validating the environment:
     *
     * <pre>{@code
     * // In your app, set env vars and call:
     * Qredex qredex = Qredex.bootstrap();
     * }</pre>
     *
     * @return a fully configured {@link Qredex} client
     * @throws QredexConfigurationException if required environment variables are missing or invalid
     */
    public static Qredex bootstrap() {
        String clientId = System.getenv("QREDEX_CLIENT_ID");
        String clientSecret = System.getenv("QREDEX_CLIENT_SECRET");
        String rawEnv = System.getenv("QREDEX_ENVIRONMENT");
        String scope = System.getenv("QREDEX_SCOPE");

        if (clientId == null || clientId.trim().isEmpty()) {
            throw new QredexConfigurationException(
                "Qredex.bootstrap() requires the QREDEX_CLIENT_ID environment variable.");
        }
        if (clientSecret == null || clientSecret.trim().isEmpty()) {
            throw new QredexConfigurationException(
                "Qredex.bootstrap() requires the QREDEX_CLIENT_SECRET environment variable.");
        }

        QredexConfig.Builder configBuilder = QredexConfig.builder()
            .clientId(clientId.trim())
            .clientSecret(clientSecret.trim())
            .environment(QredexEnvironment.fromString(rawEnv));

        if (scope != null && !scope.trim().isEmpty()) {
            configBuilder.scope(scope.trim());
        }

        return init(configBuilder.build());
    }

    // -------------------------------------------------------------------------
    // Resource accessors
    // -------------------------------------------------------------------------

    /**
     * Auth surface for explicit token issuance and cache control.
     * Normal usage does not require calling this; auth is managed automatically.
     */
    public QredexAuthClient auth() { return auth; }

    /** Creator resource operations: create, get, list. */
    public CreatorsClient creators() { return creators; }

    /** Influence link resource operations: create, get, list, getStats. */
    public LinksClient links() { return links; }

    /** IIT and PIT intent operations: issueInfluenceIntentToken, lockPurchaseIntent. */
    public IntentsClient intents() { return intents; }

    /** Order attribution operations: recordPaidOrder, list, getDetails. */
    public OrdersClient orders() { return orders; }

    /** Refund ingestion: recordRefund. */
    public RefundsClient refunds() { return refunds; }

    /** Returns the resolved configuration for this client instance. */
    public QredexConfig getConfig() { return config; }

    // -------------------------------------------------------------------------
    // Builder
    // -------------------------------------------------------------------------

    /**
     * Builder for {@link Qredex}.
     *
     * <p>Delegates to {@link QredexConfig.Builder}. All options documented there apply here.
     */
    public static final class Builder {

        private final QredexConfig.Builder configBuilder = QredexConfig.builder();

        private Builder() {}

        /** Required. Your Qredex integration client ID. */
        public Builder clientId(String clientId) { configBuilder.clientId(clientId); return this; }

        /** Required. Your Qredex integration client secret. Never logged. */
        public Builder clientSecret(String clientSecret) { configBuilder.clientSecret(clientSecret); return this; }

        /** Optional OAuth scope. Defaults to server-side scope for your credentials. */
        public Builder scope(String scope) { configBuilder.scope(scope); return this; }

        /** API environment. Defaults to {@link QredexEnvironment#PRODUCTION}. */
        public Builder environment(QredexEnvironment environment) { configBuilder.environment(environment); return this; }

        /** Overrides the base URL. Useful for testing against a local or staging server. */
        public Builder baseUrl(String baseUrl) { configBuilder.baseUrl(baseUrl); return this; }

        /** HTTP request timeout in milliseconds. Default is 10,000 ms. */
        public Builder timeoutMs(int timeoutMs) { configBuilder.timeoutMs(timeoutMs); return this; }

        /** Maximum auth retry attempts on transient failures. Default is 3. */
        public Builder maxAuthRetries(int maxAuthRetries) { configBuilder.maxAuthRetries(maxAuthRetries); return this; }

        /** Optional string appended to the SDK {@code User-Agent} header. */
        public Builder userAgentSuffix(String userAgentSuffix) { configBuilder.userAgentSuffix(userAgentSuffix); return this; }

        /** Optional logger for sanitized request/response diagnostics. Secrets are redacted. */
        public Builder logger(QredexLogger logger) { configBuilder.logger(logger); return this; }

        /**
         * Builds the {@link Qredex} client. Validates configuration eagerly.
         *
         * @throws QredexConfigurationException if required options are missing or invalid
         */
        public Qredex build() {
            return new Qredex(configBuilder.build());
        }
    }

    // -------------------------------------------------------------------------
    // Auth surface
    // -------------------------------------------------------------------------

    /**
     * Auth helper for explicit token control.
     * Provides token issuance and cache management for advanced use cases.
     */
    public static final class QredexAuthClient {

        private final TokenProvider tokenProvider;

        private QredexAuthClient(TokenProvider tokenProvider) {
            this.tokenProvider = tokenProvider;
        }

        /**
         * Issues a new client-credentials token and caches it.
         * Useful for warming the token cache, diagnostic checks, or token inspection.
         *
         * @return the raw {@link OAuthTokenResponse}. Does not contain the client secret.
         */
        public OAuthTokenResponse issueToken() {
            return tokenProvider.issueToken();
        }

        /**
         * Clears the in-memory token cache.
         * The next API call will acquire a fresh token automatically.
         */
        public void clearTokenCache() {
            tokenProvider.clearTokenCache();
        }
    }
}
