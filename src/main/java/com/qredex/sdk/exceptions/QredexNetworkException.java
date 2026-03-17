/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.exceptions;

/**
 * Thrown when a network or transport error occurs before a valid API response is received.
 *
 * <p>Examples: connection timeout, DNS failure, SSL handshake error.
 * No HTTP status is available on this exception.
 */
public class QredexNetworkException extends QredexException {

    public QredexNetworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public QredexNetworkException(String message) {
        super(message);
    }
}
