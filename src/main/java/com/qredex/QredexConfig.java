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

import com.qredex.exceptions.QredexConfigurationException;
import com.qredex.model.standards.QredexScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Immutable configuration for the {@link Qredex} client.
 *
 * <p>Obtain via {@link #builder()} or {@link Qredex#builder()}:
 * <pre>{@code
 * QredexConfig config = QredexConfig.builder()
 *     .clientId("my-client-id")
 *     .clientSecret("my-client-secret")
 *     .environment(QredexEnvironment.PRODUCTION)
 *     .build();
 * }</pre>
 */
public final class QredexConfig {

    private final String clientId;
    private final String clientSecret;
    private final String scope;
    private final QredexEnvironment environment;
    private final String baseUrl;
    private final int timeoutMs;
    private final int maxAuthRetries;
    private final String userAgentSuffix;
    private final QredexLogger logger;

    private QredexConfig(Builder builder) {
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.scope = builder.scope;
        this.environment = builder.environment != null ? builder.environment : QredexEnvironment.PRODUCTION;
        this.baseUrl = builder.baseUrl != null ? builder.baseUrl : this.environment.getDefaultBaseUrl();
        this.timeoutMs = builder.timeoutMs > 0 ? builder.timeoutMs : 10_000;
        this.maxAuthRetries = builder.maxAuthRetries > 0 ? builder.maxAuthRetries : 3;
        this.userAgentSuffix = builder.userAgentSuffix;
        this.logger = builder.logger;
    }

    /** Client ID for client-credentials auth. Never logged. */
    @NotNull
    public String getClientId() { return clientId; }

    /** Client secret for client-credentials auth. Never logged. */
    @NotNull
    public String getClientSecret() { return clientSecret; }

    /** Optional OAuth scope string. When {@code null} the server default is used. */
    @Nullable
    public String getScope() { return scope; }

    /** Resolved Qredex environment. */
    @NotNull
    public QredexEnvironment getEnvironment() { return environment; }

    /** Effective base URL (environment default or explicit override). */
    @NotNull
    public String getBaseUrl() { return baseUrl; }

    /** HTTP request timeout in milliseconds. Default is 10,000 ms. */
    public int getTimeoutMs() { return timeoutMs; }

    /** Maximum number of auth token fetch retries. Default is 3. */
    public int getMaxAuthRetries() { return maxAuthRetries; }

    /** Optional string appended to the {@code User-Agent} header. */
    @Nullable
    public String getUserAgentSuffix() { return userAgentSuffix; }

    /** Optional logger for sanitized SDK diagnostics. Secrets are never logged. */
    @Nullable
    public QredexLogger getLogger() { return logger; }

    @NotNull
    public static Builder builder() { return new Builder(); }

    /** Creates a builder pre-populated with this config's values. */
    @NotNull
    public Builder toBuilder() {
        return new Builder()
            .clientId(clientId)
            .clientSecret(clientSecret)
            .scope(scope)
            .environment(environment)
            .baseUrl(baseUrl)
            .timeoutMs(timeoutMs)
            .maxAuthRetries(maxAuthRetries)
            .userAgentSuffix(userAgentSuffix)
            .logger(logger);
    }

    public static final class Builder {
        private String clientId;
        private String clientSecret;
        private String scope;
        private QredexEnvironment environment;
        private String baseUrl;
        private int timeoutMs;
        private int maxAuthRetries;
        private String userAgentSuffix;
        private QredexLogger logger;

        private Builder() {}

        /** Required. Your Qredex integration client ID. */
        @NotNull
        public Builder clientId(@NotNull String clientId) { this.clientId = clientId; return this; }

        /** Required. Your Qredex integration client secret. Never logged. */
        @NotNull
        public Builder clientSecret(@NotNull String clientSecret) { this.clientSecret = clientSecret; return this; }

        /** Optional OAuth scope. Defaults to server-side scope for your client credentials. */
        @NotNull
        public Builder scope(@Nullable String scope) { this.scope = scope; return this; }

        /** Sets OAuth scopes from typed enum values. Replaces any previous scope string. */
        @NotNull
        public Builder scopes(@Nullable QredexScope... scopes) { this.scope = QredexScope.join(scopes); return this; }

        /** API environment. Defaults to {@link QredexEnvironment#PRODUCTION}. */
        @NotNull
        public Builder environment(@NotNull QredexEnvironment environment) { this.environment = environment; return this; }

        /** Overrides the base URL regardless of environment. Useful for testing. */
        @NotNull
        public Builder baseUrl(@NotNull String baseUrl) { this.baseUrl = baseUrl; return this; }

        /** HTTP request timeout in milliseconds. Default is 10,000 ms. */
        @NotNull
        public Builder timeoutMs(int timeoutMs) { this.timeoutMs = timeoutMs; return this; }

        /** Maximum auth retry attempts. Default is 3. */
        @NotNull
        public Builder maxAuthRetries(int maxAuthRetries) { this.maxAuthRetries = maxAuthRetries; return this; }

        /** Optional string appended to the SDK {@code User-Agent} header. */
        @NotNull
        public Builder userAgentSuffix(@Nullable String userAgentSuffix) { this.userAgentSuffix = userAgentSuffix; return this; }

        /** Optional logger for sanitized request/response diagnostics. Secrets are redacted. */
        @NotNull
        public Builder logger(@Nullable QredexLogger logger) { this.logger = logger; return this; }

        @NotNull
        public QredexConfig build() {
            if (clientId == null || clientId.trim().isEmpty()) {
                throw new QredexConfigurationException("QredexConfig requires clientId.");
            }
            if (clientSecret == null || clientSecret.trim().isEmpty()) {
                throw new QredexConfigurationException("QredexConfig requires clientSecret.");
            }
            return new QredexConfig(this);
        }
    }
}
