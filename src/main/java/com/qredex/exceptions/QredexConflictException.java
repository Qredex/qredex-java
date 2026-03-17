/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.exceptions;

/**
 * Thrown when the API returns HTTP 409: a policy or duplicate conflict rejected the request.
 *
 * <p>Common causes include {@code REJECTED_SOURCE_POLICY} and
 * {@code REJECTED_CROSS_SOURCE_DUPLICATE} ingestion decisions.
 * Inspect {@link #getErrorCode()} for the canonical Qredex rejection reason.
 */
public class QredexConflictException extends QredexApiException {

    public QredexConflictException(String message, Integer status, String errorCode,
                                    String requestId, String traceId, String responseBody) {
        super(message, status, errorCode, requestId, traceId, responseBody);
    }
}
