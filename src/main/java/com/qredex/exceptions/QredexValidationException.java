/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.exceptions;

/**
 * Thrown when the API returns HTTP 400: the request payload failed validation.
 *
 * <p>Also thrown for client-side pre-flight validation failures before a request is sent.
 */
public class QredexValidationException extends QredexApiException {

    public QredexValidationException(String message, Integer status, String errorCode,
                                      String requestId, String traceId, String responseBody) {
        super(message, status, errorCode, requestId, traceId, responseBody);
    }

    /** Creates a client-side validation exception (before any HTTP request is made). */
    public QredexValidationException(String message) {
        super(message, null, "validation_error", null, null, null);
    }
}
