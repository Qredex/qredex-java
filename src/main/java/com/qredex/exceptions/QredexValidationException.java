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
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 *  Licensed under the Apache License, Version 2.0. See LICENSE for the full license text.
 *  You may not use this file except in compliance with that License.
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 *  License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific language governing permissions
 *  and limitations under the License.
 *
 *  If you need additional information or have any questions, please email: copyright@qredex.com
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
