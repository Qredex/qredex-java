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

import com.qredex.QredexCallOptions;
import com.qredex.QredexConfig;
import com.qredex.QredexLogger;
import com.qredex.exceptions.QredexNetworkException;
import com.qredex.exceptions.QredexRateLimitException;
import com.qredex.model.response.OAuthTokenResponse;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Internal OkHttp-based transport. Handles serialization, headers, and error parsing.
 * Not part of the public SDK surface.
 */
public final class HttpTransport {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType FORM = MediaType.parse("application/x-www-form-urlencoded");
    private static final String HEADER_REQUEST_ID = "X-Request-Id";
    private static final String HEADER_TRACE_ID   = "X-Trace-Id";
    private static final String HEADER_RETRY_AFTER = "Retry-After";
    private static final String HEADER_IDEMPOTENCY_KEY = "Idempotency-Key";

    private final OkHttpClient httpClient;
    private final String baseUrl;
    private final String userAgent;
    private final QredexLogger logger;

    public HttpTransport(QredexConfig config) {
        this.baseUrl = trimTrailingSlash(config.getBaseUrl());
        this.userAgent = QredexUserAgent.build(config.getUserAgentSuffix());
        this.logger = config.getLogger();
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(config.getTimeoutMs(), TimeUnit.MILLISECONDS)
            .readTimeout(config.getTimeoutMs(), TimeUnit.MILLISECONDS)
            .writeTimeout(config.getTimeoutMs(), TimeUnit.MILLISECONDS)
            .build();
    }

    /** Fetches an OAuth token using Basic auth (client credentials). */
    public OAuthTokenResponse fetchToken(String clientId, String clientSecret, String scope) {
        StringBuilder formBody = new StringBuilder("grant_type=client_credentials");
        if (scope != null && !scope.trim().isEmpty()) {
            formBody.append("&scope=").append(urlEncode(scope.trim()));
        }

        String credentials = Base64.getEncoder().encodeToString(
            (clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        Request request = new Request.Builder()
            .url(baseUrl + "/api/v1/auth/token")
            .addHeader("Authorization", "Basic " + credentials)
            .addHeader("User-Agent", userAgent)
            .addHeader("Accept", "application/json")
            .post(RequestBody.create(FORM, formBody.toString()))
            .build();

        return execute(request, OAuthTokenResponse.class);
    }

    /** Executes an authenticated JSON GET request with optional query parameters. */
    public <T> T get(String path, Map<String, String> query, String bearerToken, Class<T> responseType) {
        return get(path, query, bearerToken, responseType, null);
    }

    /** Executes an authenticated JSON GET request with optional query parameters and request options. */
    public <T> T get(String path, Map<String, String> query, String bearerToken, Class<T> responseType, QredexCallOptions options) {
        HttpUrl parsed = HttpUrl.parse(baseUrl + path);
        if (parsed == null) {
            throw new QredexNetworkException("Invalid URL: " + baseUrl + path);
        }
        HttpUrl.Builder urlBuilder = parsed.newBuilder();
        if (query != null) {
            for (Map.Entry<String, String> entry : query.entrySet()) {
                if (entry.getValue() != null) {
                    urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
                }
            }
        }

        Request request = new Request.Builder()
            .url(urlBuilder.build())
            .addHeader("Authorization", bearerToken)
            .addHeader("User-Agent", userAgent)
            .addHeader("Accept", "application/json")
            .get()
            .build();

        request = applyCallOptions(request, options);

        log("GET " + path);
        return execute(request, responseType, options);
    }

    /** Shuts down the HTTP client's connection pool and dispatcher. */
    public void close() {
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
    }

    /** Executes an authenticated JSON POST request. */
    public <T> T post(String path, Object body, String bearerToken, Class<T> responseType) {
        return post(path, body, bearerToken, responseType, null);
    }

    /** Executes an authenticated JSON POST request with optional request controls. */
    public <T> T post(String path, Object body, String bearerToken, Class<T> responseType, QredexCallOptions options) {
        try {
            String json = JsonMapper.INSTANCE.writeValueAsString(body);
            Request request = new Request.Builder()
                .url(baseUrl + path)
                .addHeader("Authorization", bearerToken)
                .addHeader("User-Agent", userAgent)
                .addHeader("Accept", "application/json")
                .post(RequestBody.create(JSON, json))
                .build();

            request = applyCallOptions(request, options);

            log("POST " + path);
            return execute(request, responseType, options);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new QredexNetworkException("Failed to serialize request body: " + e.getMessage(), e);
        }
    }

    private <T> T execute(Request request, Class<T> responseType, QredexCallOptions options) {
        OkHttpClient client = httpClient;
        if (options != null && options.getTimeoutMs() != null) {
            client = httpClient.newBuilder()
                .connectTimeout(options.getTimeoutMs(), TimeUnit.MILLISECONDS)
                .readTimeout(options.getTimeoutMs(), TimeUnit.MILLISECONDS)
                .writeTimeout(options.getTimeoutMs(), TimeUnit.MILLISECONDS)
                .build();
        }
        try (Response response = client.newCall(request).execute()) {
            String requestId = response.header(HEADER_REQUEST_ID);
            String traceId   = response.header(HEADER_TRACE_ID);
            ResponseBody responseBody = response.body();
            String body = responseBody != null ? responseBody.string() : "";
            int status = response.code();

            logResponse(request.method(), request.url().encodedPath(), status, requestId);

            if (response.isSuccessful()) {
                if (body.isEmpty()) {
                    throw new QredexNetworkException(
                        "Received empty response body for " + request.method() + " " + request.url().encodedPath()
                            + " when " + responseType.getSimpleName() + " was expected.");
                }
                try {
                    return JsonMapper.INSTANCE.readValue(body, responseType);
                } catch (IOException e) {
                    throw new QredexNetworkException("Failed to parse API response: " + e.getMessage(), e);
                }
            }

            if (status == 429) {
                String retryAfterHeader = response.header(HEADER_RETRY_AFTER);
                Long retryAfterSeconds = parseRetryAfter(retryAfterHeader);
                throw ApiErrorFactory.rateLimited(status, body, requestId, traceId, retryAfterSeconds);
            }

            throw ApiErrorFactory.fromHttpResponse(status, body, requestId, traceId);

        } catch (IOException e) {
            throw new QredexNetworkException("Network error: " + e.getMessage(), e);
        }
    }

    private void log(String message) {
        if (logger != null) logger.debug("[qredex] " + message);
    }

    private void logResponse(String method, String path, int status, String requestId) {
        if (logger != null) {
            String msg = "[qredex] " + method + " " + path + " -> " + status;
            if (requestId != null) msg += " requestId=" + requestId;
            if (status >= 400) logger.warn(msg);
            else logger.debug(msg);
        }
    }

    private Request applyCallOptions(Request request, QredexCallOptions options) {
        if (options == null) {
            return request;
        }
        Request.Builder builder = request.newBuilder();
        if (options.getRequestId() != null) {
            builder.header(HEADER_REQUEST_ID, options.getRequestId());
        }
        if (options.getTraceId() != null) {
            builder.header(HEADER_TRACE_ID, options.getTraceId());
        }
        if (options.getIdempotencyKey() != null) {
            builder.header(HEADER_IDEMPOTENCY_KEY, options.getIdempotencyKey());
        }
        for (Map.Entry<String, String> entry : options.getHeaders().entrySet()) {
            builder.header(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    private static Long parseRetryAfter(String value) {
        if (value == null) return null;
        try { return Long.parseLong(value.trim()); }
        catch (NumberFormatException ignored) { return null; }
    }

    private static String trimTrailingSlash(String url) {
        return url != null && url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private static String urlEncode(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return value;
        }
    }
}
