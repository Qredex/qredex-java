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
package com.qredex.exceptions;

import org.jetbrains.annotations.Nullable;

/**
 * Thrown when the API returns HTTP 429: the caller has exceeded its rate limit.
 *
 * <p>{@link #getRetryAfterSeconds()} provides the server-indicated back-off interval
 * when a {@code Retry-After} header is present.
 */
public class QredexRateLimitException extends QredexApiException {

    private final Long retryAfterSeconds;

    public QredexRateLimitException(String message, Integer status, String errorCode,
                                     String requestId, String traceId, String responseBody,
                                     Long retryAfterSeconds) {
        super(message, status, errorCode, requestId, traceId, responseBody);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    /**
     * Server-indicated retry interval in seconds from the {@code Retry-After} response header,
     * or {@code null} if the header was absent.
     */
    @Nullable
    public Long getRetryAfterSeconds() { return retryAfterSeconds; }
}
