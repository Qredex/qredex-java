/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.exceptions;

/**
 * Thrown when the SDK is configured with invalid or missing required options.
 *
 * <p>This is a fast-fail exception raised before any network activity.
 */
public class QredexConfigurationException extends QredexException {

    public QredexConfigurationException(String message) {
        super(message);
    }
}
