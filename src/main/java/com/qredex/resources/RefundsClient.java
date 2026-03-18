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
import com.qredex.internal.HttpTransport;
import com.qredex.internal.TokenProvider;
import com.qredex.exceptions.QredexValidationException;
import com.qredex.model.request.RecordRefundRequest;
import com.qredex.model.response.OrderAttributionResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @NotNull
    public OrderAttributionResponse recordRefund(@NotNull RecordRefundRequest request) {
        return recordRefund(request, null);
    }

    @NotNull
    public OrderAttributionResponse recordRefund(
        @NotNull RecordRefundRequest request,
        @Nullable QredexCallOptions options
    ) {
        if (request == null) {
            throw new QredexValidationException("request must not be null.");
        }
        return transport.post(
            "/api/v1/integrations/orders/refund",
            request,
            tokenProvider.getAuthorizationHeader(),
            OrderAttributionResponse.class,
            options);
    }
}
