/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.exceptions;

/**
 * Thrown when the API returns HTTP 401: invalid or missing credentials or access token.
 */
public class QredexAuthenticationException extends QredexApiException {

    public QredexAuthenticationException(String message, Integer status, String errorCode,
                                          String requestId, String traceId, String responseBody) {
        super(message, status, errorCode, requestId, traceId, responseBody);
    }
}
