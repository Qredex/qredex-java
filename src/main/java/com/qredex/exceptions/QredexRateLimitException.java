/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.exceptions;

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
    public Long getRetryAfterSeconds() { return retryAfterSeconds; }
}
