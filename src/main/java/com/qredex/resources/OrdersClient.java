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
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.resources;

import com.qredex.exceptions.QredexValidationException;
import com.qredex.internal.HttpTransport;
import com.qredex.internal.QueryParams;
import com.qredex.internal.TokenProvider;
import com.qredex.model.request.ListOrdersRequest;
import com.qredex.model.request.RecordPaidOrderRequest;
import com.qredex.model.response.OrderAttributionDetailsResponse;
import com.qredex.model.response.OrderAttributionPageResponse;
import com.qredex.model.response.OrderAttributionResponse;

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
    public OrderAttributionResponse recordPaidOrder(RecordPaidOrderRequest request) {
        return transport.post(
            "/api/v1/integrations/orders/paid",
            request,
            tokenProvider.getAuthorizationHeader(),
            OrderAttributionResponse.class);
    }

    /**
     * Lists order attribution records with optional pagination.
     *
     * @param request pagination params; use {@link ListOrdersRequest#defaults()} for none
     * @return paginated {@link OrderAttributionPageResponse}
     */
    public OrderAttributionPageResponse list(ListOrdersRequest request) {
        if (request == null) request = ListOrdersRequest.defaults();
        return transport.get(
            "/api/v1/integrations/orders",
            QueryParams.create()
                .add("page", request.getPage())
                .add("size", request.getSize())
                .build(),
            tokenProvider.getAuthorizationHeader(),
            OrderAttributionPageResponse.class);
    }

    /**
     * Retrieves the detailed attribution record for a single order attribution ID.
     *
     * <p>Includes score breakdown, integrity signals, and the attribution event timeline.
     *
     * @param orderAttributionId the UUID of the order attribution record
     * @return the {@link OrderAttributionDetailsResponse}
     */
    public OrderAttributionDetailsResponse getDetails(String orderAttributionId) {
        if (orderAttributionId == null || orderAttributionId.trim().isEmpty()) {
            throw new QredexValidationException("orderAttributionId must not be blank.");
        }
        return transport.get(
            "/api/v1/integrations/orders/" + encode(orderAttributionId) + "/details",
            null,
            tokenProvider.getAuthorizationHeader(),
            OrderAttributionDetailsResponse.class);
    }

    private static String encode(String value) {
        try { return java.net.URLEncoder.encode(value, "UTF-8"); }
        catch (java.io.UnsupportedEncodingException e) { return value; }
    }
}
