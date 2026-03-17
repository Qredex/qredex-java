/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.exceptions;

/**
 * Base class for all exceptions originating from a Qredex API response.
 *
 * <p>All subclasses carry the HTTP status, {@code error_code}, and correlation IDs
 * from the response. Use the specific subclasses ({@link QredexAuthenticationException},
 * {@link QredexValidationException}, etc.) for programmatic handling.
 */
public class QredexApiException extends QredexException {

    public QredexApiException(String message, Integer status, String errorCode,
                               String requestId, String traceId, String responseBody) {
        super(message, status, errorCode, requestId, traceId, responseBody);
    }
}
