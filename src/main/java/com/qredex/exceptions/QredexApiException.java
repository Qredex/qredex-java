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
