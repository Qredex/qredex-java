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

import com.qredex.exceptions.QredexValidationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Per-request operational controls for the Java SDK.
 *
 * <p>Use this to attach correlation headers, deterministic idempotency keys, bounded timeout
 * overrides, and additional non-reserved headers without mutating the shared client.
 */
public final class QredexCallOptions {

    private final Map<String, String> headers;
    private final String idempotencyKey;
    private final String requestId;
    private final Integer timeoutMs;
    private final String traceId;

    private QredexCallOptions(Builder builder) {
        this.headers = Collections.unmodifiableMap(new LinkedHashMap<>(builder.headers));
        this.idempotencyKey = builder.idempotencyKey;
        this.requestId = builder.requestId;
        this.timeoutMs = builder.timeoutMs;
        this.traceId = builder.traceId;
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @NotNull
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Nullable
    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    @Nullable
    public String getRequestId() {
        return requestId;
    }

    @Nullable
    public Integer getTimeoutMs() {
        return timeoutMs;
    }

    @Nullable
    public String getTraceId() {
        return traceId;
    }

    public static final class Builder {
        private static final String HEADER_ACCEPT = "accept";
        private static final String HEADER_AUTHORIZATION = "authorization";
        private static final String HEADER_CONTENT_TYPE = "content-type";
        private static final String HEADER_IDEMPOTENCY_KEY = "idempotency-key";
        private static final String HEADER_REQUEST_ID = "x-request-id";
        private static final String HEADER_TRACE_ID = "x-trace-id";
        private static final String HEADER_USER_AGENT = "user-agent";

        private final Map<String, String> headers = new LinkedHashMap<>();
        private String idempotencyKey;
        private String requestId;
        private Integer timeoutMs;
        private String traceId;

        private Builder() {
        }

        @NotNull
        public Builder header(@NotNull String name, @NotNull String value) {
            String normalized = normalizeHeaderName(name);
            if (isReservedHeader(normalized)) {
                throw new QredexValidationException(
                    "header '" + normalized + "' is reserved. Use requestId, traceId, or idempotencyKey instead.");
            }
            if (value.trim().isEmpty()) {
                throw new QredexValidationException("header '" + normalized + "' must not be blank.");
            }
            headers.put(normalized, value.trim());
            return this;
        }

        @NotNull
        public Builder headers(@Nullable Map<String, String> values) {
            if (values == null) {
                return this;
            }
            for (Map.Entry<String, String> entry : values.entrySet()) {
                header(entry.getKey(), entry.getValue());
            }
            return this;
        }

        @NotNull
        public Builder idempotencyKey(@Nullable String value) {
            this.idempotencyKey = trimToNull(value, "idempotencyKey");
            return this;
        }

        @NotNull
        public Builder requestId(@Nullable String value) {
            this.requestId = trimToNull(value, "requestId");
            return this;
        }

        @NotNull
        public Builder timeoutMs(@Nullable Integer value) {
            if (value != null && value.intValue() <= 0) {
                throw new QredexValidationException("timeoutMs must be greater than 0.");
            }
            this.timeoutMs = value;
            return this;
        }

        @NotNull
        public Builder traceId(@Nullable String value) {
            this.traceId = trimToNull(value, "traceId");
            return this;
        }

        @NotNull
        public QredexCallOptions build() {
            return new QredexCallOptions(this);
        }

        private static boolean isReservedHeader(String normalized) {
            return HEADER_ACCEPT.equals(normalized)
                || HEADER_AUTHORIZATION.equals(normalized)
                || HEADER_CONTENT_TYPE.equals(normalized)
                || HEADER_IDEMPOTENCY_KEY.equals(normalized)
                || HEADER_REQUEST_ID.equals(normalized)
                || HEADER_TRACE_ID.equals(normalized)
                || HEADER_USER_AGENT.equals(normalized);
        }

        private static String normalizeHeaderName(String value) {
            if (value == null || value.trim().isEmpty()) {
                throw new QredexValidationException("header name must not be blank.");
            }
            return value.trim().toLowerCase(Locale.ROOT);
        }

        private static String trimToNull(String value, String field) {
            if (value == null) {
                return null;
            }
            String trimmed = value.trim();
            if (trimmed.isEmpty()) {
                throw new QredexValidationException(field + " must not be blank.");
            }
            return trimmed;
        }
    }
}
