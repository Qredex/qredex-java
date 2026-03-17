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
package com.qredex.internal;

import com.qredex.exceptions.*;
import com.qredex.model.response.ApiErrorResponse;

import java.io.IOException;

/** Parses API error responses into the structured Qredex exception hierarchy. */
public final class ApiErrorFactory {

    public static QredexApiException fromHttpResponse(
            int status,
            String body,
            String requestId,
            String traceId) {

        String errorCode = null;
        String message = null;

        if (body != null && !body.isEmpty()) {
            try {
                ApiErrorResponse err = JsonMapper.INSTANCE.readValue(body, ApiErrorResponse.class);
                errorCode = err.getErrorCode();
                message = err.getMessage();
            } catch (IOException ignored) {
                // non-JSON body — use raw text as message
                message = body;
            }
        }

        if (message == null || message.isEmpty()) {
            message = defaultMessage(status);
        }

        switch (status) {
            case 400:
                return new QredexValidationException(message, status, errorCode, requestId, traceId, body);
            case 401:
                return new QredexAuthenticationException(message, status, errorCode, requestId, traceId, body);
            case 403:
                return new QredexAuthorizationException(message, status, errorCode, requestId, traceId, body);
            case 404:
                return new QredexNotFoundException(message, status, errorCode, requestId, traceId, body);
            case 409:
                return new QredexConflictException(message, status, errorCode, requestId, traceId, body);
            case 429: {
                return new QredexRateLimitException(message, status, errorCode, requestId, traceId, body, null);
            }
            default:
                return new QredexApiException(message, status, errorCode, requestId, traceId, body);
        }
    }

    public static QredexRateLimitException rateLimited(
            int status, String body, String requestId, String traceId, Long retryAfterSeconds) {
        String message = "Rate limit exceeded.";
        String errorCode = null;
        if (body != null && !body.isEmpty()) {
            try {
                ApiErrorResponse err = JsonMapper.INSTANCE.readValue(body, ApiErrorResponse.class);
                if (err.getMessage() != null) message = err.getMessage();
                errorCode = err.getErrorCode();
            } catch (IOException ignored) {}
        }
        return new QredexRateLimitException(message, status, errorCode, requestId, traceId, body, retryAfterSeconds);
    }

    private static String defaultMessage(int status) {
        switch (status) {
            case 400: return "Bad request.";
            case 401: return "Authentication failed.";
            case 403: return "Insufficient permissions.";
            case 404: return "Resource not found.";
            case 409: return "Conflict or policy rejection.";
            case 429: return "Rate limit exceeded.";
            case 500: return "Internal server error.";
            case 502: return "Bad gateway.";
            case 503: return "Service unavailable.";
            default:  return "Unexpected API error (HTTP " + status + ").";
        }
    }

    private ApiErrorFactory() {}
}
