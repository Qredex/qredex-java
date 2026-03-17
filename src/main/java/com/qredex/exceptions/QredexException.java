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
 *  Licensed under the Apache License, Version 2.0. See LICENSE for the full license text.
 */
package com.qredex.exceptions;

/**
 * Base exception for all Qredex SDK errors.
 *
 * <p>Subclasses carry structured metadata preserved from the API response where available:
 * HTTP status, {@code error_code}, message, request ID, and trace ID.
 */
public class QredexException extends RuntimeException {

    private final Integer status;
    private final String errorCode;
    private final String requestId;
    private final String traceId;
    private final String responseBody;

    /** Constructs a plain exception with message only. */
    public QredexException(String message) {
        super(message);
        this.status = null;
        this.errorCode = null;
        this.requestId = null;
        this.traceId = null;
        this.responseBody = null;
    }

    /** Constructs an exception with message and cause. */
    public QredexException(String message, Throwable cause) {
        super(message, cause);
        this.status = null;
        this.errorCode = null;
        this.requestId = null;
        this.traceId = null;
        this.responseBody = null;
    }

    /** Constructs a fully-structured exception from API response metadata. */
    public QredexException(String message, Integer status, String errorCode,
                           String requestId, String traceId, String responseBody) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
        this.requestId = requestId;
        this.traceId = traceId;
        this.responseBody = responseBody;
    }

    /** HTTP status code from the API response, or {@code null} for non-HTTP failures. */
    public Integer getStatus() { return status; }

    /** Qredex {@code error_code} field from the API error body, or {@code null} if absent. */
    public String getErrorCode() { return errorCode; }

    /** Qredex {@code X-Request-Id} header value, or {@code null} if absent. */
    public String getRequestId() { return requestId; }

    /** Qredex {@code X-Trace-Id} header value, or {@code null} if absent. */
    public String getTraceId() { return traceId; }

    /** Raw response body text, useful for debugging. Never contains secrets. */
    public String getResponseBody() { return responseBody; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(": ").append(getMessage());
        if (status != null) sb.append(" [status=").append(status).append("]");
        if (errorCode != null) sb.append(" [errorCode=").append(errorCode).append("]");
        if (requestId != null) sb.append(" [requestId=").append(requestId).append("]");
        if (traceId != null) sb.append(" [traceId=").append(traceId).append("]");
        return sb.toString();
    }
}
