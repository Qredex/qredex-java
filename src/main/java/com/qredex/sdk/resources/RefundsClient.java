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
package com.qredex.sdk.resources;

import com.qredex.sdk.exceptions.QredexValidationException;
import com.qredex.sdk.internal.HttpTransport;
import com.qredex.sdk.internal.TokenProvider;
import com.qredex.sdk.model.request.RecordRefundRequest;
import com.qredex.sdk.model.response.OrderAttributionResponse;

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
        if (request == null) throw new QredexValidationException("RecordRefundRequest must not be null.");
        if (request.getStoreId() == null || request.getStoreId().trim().isEmpty()) {
            throw new QredexValidationException("RecordRefundRequest requires storeId.");
        }
        if (request.getExternalOrderId() == null || request.getExternalOrderId().trim().isEmpty()) {
            throw new QredexValidationException("RecordRefundRequest requires externalOrderId.");
        }
        if (request.getExternalRefundId() == null || request.getExternalRefundId().trim().isEmpty()) {
            throw new QredexValidationException("RecordRefundRequest requires externalRefundId.");
        }
        return transport.post(
            "/api/v1/integrations/orders/refund",
            request,
            tokenProvider.getAuthorizationHeader(),
            OrderAttributionResponse.class);
    }
}
