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
package com.qredex.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qredex.exceptions.QredexValidationException;

/**
 * Request body for recording a refund against a previously recorded paid order.
 *
 * <p>Order attribution is matched by {@code (store_id, external_order_id)}.
 *
 * <pre>{@code
 * RecordRefundRequest req = RecordRefundRequest.builder()
 *     .storeId("61abc354-dd8d-4a23-be02-ece77b1b4da6")
 *     .externalOrderId("order-100045")
 *     .externalRefundId("refund-987")
 *     .refundTotal(110.00)
 *     .build();
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class RecordRefundRequest {

    @JsonProperty("store_id")
    private final String storeId;

    @JsonProperty("external_order_id")
    private final String externalOrderId;

    @JsonProperty("external_refund_id")
    private final String externalRefundId;

    @JsonProperty("refund_total")
    private final Double refundTotal;

    @JsonProperty("refunded_at")
    private final String refundedAt;

    private RecordRefundRequest(Builder builder) {
        this.storeId = builder.storeId;
        this.externalOrderId = builder.externalOrderId;
        this.externalRefundId = builder.externalRefundId;
        this.refundTotal = builder.refundTotal;
        this.refundedAt = builder.refundedAt;
    }

    public String getStoreId() { return storeId; }
    public String getExternalOrderId() { return externalOrderId; }
    public String getExternalRefundId() { return externalRefundId; }
    public Double getRefundTotal() { return refundTotal; }
    public String getRefundedAt() { return refundedAt; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String storeId;
        private String externalOrderId;
        private String externalRefundId;
        private Double refundTotal;
        private String refundedAt;

        private Builder() {}

        /** Required. Store UUID matching the original order. */
        public Builder storeId(String storeId) { this.storeId = storeId; return this; }

        /** Required. External order ID matching the original paid order. */
        public Builder externalOrderId(String externalOrderId) { this.externalOrderId = externalOrderId; return this; }

        /** Required. Stable external refund ID from your platform. */
        public Builder externalRefundId(String externalRefundId) { this.externalRefundId = externalRefundId; return this; }

        /** Optional total amount refunded. */
        public Builder refundTotal(double refundTotal) { this.refundTotal = refundTotal; return this; }

        /** Optional ISO-8601 datetime the refund was issued. */
        public Builder refundedAt(String refundedAt) { this.refundedAt = refundedAt; return this; }

        public RecordRefundRequest build() {
            if (storeId == null || storeId.trim().isEmpty())
                throw new QredexValidationException("RecordRefundRequest requires storeId.");
            if (externalOrderId == null || externalOrderId.trim().isEmpty())
                throw new QredexValidationException("RecordRefundRequest requires externalOrderId.");
            if (externalRefundId == null || externalRefundId.trim().isEmpty())
                throw new QredexValidationException("RecordRefundRequest requires externalRefundId.");
            return new RecordRefundRequest(this);
        }
    }

    @Override
    public String toString() {
        return "RecordRefundRequest{storeId='" + storeId + "', externalOrderId='" + externalOrderId
                + "', externalRefundId='" + externalRefundId + "', refundTotal=" + refundTotal + "}";
    }
}
