/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.exceptions;

/**
 * Thrown when the API returns HTTP 404: the requested resource does not exist.
 */
public class QredexNotFoundException extends QredexApiException {

    public QredexNotFoundException(String message, Integer status, String errorCode,
                                    String requestId, String traceId, String responseBody) {
        super(message, status, errorCode, requestId, traceId, responseBody);
    }
}
