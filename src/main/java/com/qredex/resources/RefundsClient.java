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

import com.qredex.internal.HttpTransport;
import com.qredex.internal.TokenProvider;
import com.qredex.model.request.RecordRefundRequest;
import com.qredex.model.response.OrderAttributionResponse;

/**
 * Refund ingestion operations for previously recorded paid orders.
 *
 * <p>Obtain via {@code qredex.refunds()}.
 */
public final class RefundsClient {

    private final HttpTransport transport;
    private final TokenProvider tokenProvider;

    public RefundsClient(HttpTransport transport, TokenProvider tokenProvider) {
        this.transport = transport;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Records a refund against a previously recorded paid order.
     *
     * <p>Order attribution is matched by {@code (store_id, external_order_id)}.
     * Safe to retry on timeout: idempotent on the same {@code (store_id, external_refund_id)}.
     *
     * @param request refund parameters; {@code storeId}, {@code externalOrderId},
     *                and {@code externalRefundId} are required
     * @return the updated {@link OrderAttributionResponse}
     */
    public OrderAttributionResponse recordRefund(RecordRefundRequest request) {
        return transport.post(
            "/api/v1/integrations/orders/refund",
            request,
            tokenProvider.getAuthorizationHeader(),
            OrderAttributionResponse.class);
    }
}
