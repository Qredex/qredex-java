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
package com.qredex.resources;

import com.qredex.QredexCallOptions;
import com.qredex.exceptions.QredexValidationException;
import com.qredex.internal.HttpTransport;
import com.qredex.internal.QueryParams;
import com.qredex.internal.TokenProvider;
import com.qredex.model.request.ListOrdersRequest;
import com.qredex.model.request.RecordPaidOrderRequest;
import com.qredex.model.response.OrderAttributionDetailsResponse;
import com.qredex.model.response.OrderAttributionPageResponse;
import com.qredex.model.response.OrderAttributionResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Order attribution read and paid-order ingestion operations.
 *
 * <p>Obtain via {@code qredex.orders()}.
 *
 * <h3>Ingestion decisions</h3>
 * The API returns HTTP 200 for both {@code INGESTED} and {@code IDEMPOTENT} decisions.
 * It returns HTTP 409 for {@code REJECTED_SOURCE_POLICY} and
 * {@code REJECTED_CROSS_SOURCE_DUPLICATE}. Inspect the {@code error_code} on a
 * {@link com.qredex.exceptions.QredexConflictException} for the specific rejection reason.
 */
public final class OrdersClient {

    private final HttpTransport transport;
    private final TokenProvider tokenProvider;

    public OrdersClient(HttpTransport transport, TokenProvider tokenProvider) {
        this.transport = transport;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Records a paid order into the Qredex Integrations API.
     *
     * <p>Include {@code purchaseIntentToken} (PIT) on the request for creator attribution.
     * Safe to retry on timeout: idempotent on the same {@code (store_id, external_order_id)}.
     *
     * @param request order details; {@code storeId}, {@code externalOrderId}, and {@code currency} are required
     * @return the {@link OrderAttributionResponse} with resolution and integrity signals
     */
    @NotNull
    public OrderAttributionResponse recordPaidOrder(@NotNull RecordPaidOrderRequest request) {
        return recordPaidOrder(request, null);
    }

    @NotNull
    public OrderAttributionResponse recordPaidOrder(
        @NotNull RecordPaidOrderRequest request,
        @Nullable QredexCallOptions options
    ) {
        if (request == null) {
            throw new QredexValidationException("request must not be null.");
        }
        return transport.post(
            "/api/v1/integrations/orders/paid",
            request,
            tokenProvider.getAuthorizationHeader(),
            OrderAttributionResponse.class,
            options);
    }

    /**
     * Lists order attribution records with optional pagination.
     *
     * @param request pagination params; use {@link ListOrdersRequest#defaults()} for none
     * @return paginated {@link OrderAttributionPageResponse}
     */
    @NotNull
    public OrderAttributionPageResponse list() {
        return list(null, null);
    }

    @NotNull
    public OrderAttributionPageResponse list(@Nullable ListOrdersRequest request) {
        return list(request, null);
    }

    @NotNull
    public OrderAttributionPageResponse list(@Nullable ListOrdersRequest request, @Nullable QredexCallOptions options) {
        if (request == null) request = ListOrdersRequest.defaults();
        return transport.get(
            "/api/v1/integrations/orders",
            QueryParams.create()
                .add("page", request.getPage())
                .add("size", request.getSize())
                .build(),
            tokenProvider.getAuthorizationHeader(),
            OrderAttributionPageResponse.class,
            options);
    }

    /**
     * Retrieves the detailed attribution record for a single order attribution ID.
     *
     * <p>Includes score breakdown, integrity signals, and the attribution event timeline.
     *
     * @param orderAttributionId the UUID of the order attribution record
     * @return the {@link OrderAttributionDetailsResponse}
     */
    @NotNull
    public OrderAttributionDetailsResponse getDetails(@Nullable String orderAttributionId) {
        return getDetails(orderAttributionId, null);
    }

    @NotNull
    public OrderAttributionDetailsResponse getDetails(
        @Nullable String orderAttributionId,
        @Nullable QredexCallOptions options
    ) {
        if (orderAttributionId == null || orderAttributionId.trim().isEmpty()) {
            throw new QredexValidationException("orderAttributionId must not be blank.");
        }
        return transport.get(
            "/api/v1/integrations/orders/" + encode(orderAttributionId) + "/details",
            null,
            tokenProvider.getAuthorizationHeader(),
            OrderAttributionDetailsResponse.class,
            options);
    }

    private static String encode(String value) {
        try { return java.net.URLEncoder.encode(value, "UTF-8"); }
        catch (java.io.UnsupportedEncodingException e) { return value; }
    }
}
