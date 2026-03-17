/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.exceptions;

/**
 * Thrown when the API returns HTTP 403: authenticated but missing required scope or permission.
 */
public class QredexAuthorizationException extends QredexApiException {

    public QredexAuthorizationException(String message, Integer status, String errorCode,
                                         String requestId, String traceId, String responseBody) {
        super(message, status, errorCode, requestId, traceId, responseBody);
    }
}
