/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qredex.sdk.exceptions.QredexValidationException;

/**
 * Request body for recording a paid order into the Qredex Integrations API.
 *
 * <p>{@code order_source} is automatically inferred as {@code DIRECT_API} by the API.
 * Include the {@code purchase_intent_token} (PIT) for attribution.
 *
 * <pre>{@code
 * RecordPaidOrderRequest req = RecordPaidOrderRequest.builder()
 *     .storeId("61abc354-dd8d-4a23-be02-ece77b1b4da6")
 *     .externalOrderId("order-100045")
 *     .currency("USD")
 *     .totalPrice(110.00)
 *     .purchaseIntentToken(pit)
 *     .build();
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class RecordPaidOrderRequest {

    @JsonProperty("store_id")
    private final String storeId;

    @JsonProperty("external_order_id")
    private final String externalOrderId;

    @JsonProperty("order_number")
    private final String orderNumber;

    @JsonProperty("paid_at")
    private final String paidAt;

    @JsonProperty("currency")
    private final String currency;

    @JsonProperty("subtotal_price")
    private final Double subtotalPrice;

    @JsonProperty("discount_total")
    private final Double discountTotal;

    @JsonProperty("total_price")
    private final Double totalPrice;

    @JsonProperty("customer_email_hash")
    private final String customerEmailHash;

    @JsonProperty("checkout_token")
    private final String checkoutToken;

    @JsonProperty("purchase_intent_token")
    private final String purchaseIntentToken;

    private RecordPaidOrderRequest(Builder builder) {
        this.storeId = builder.storeId;
        this.externalOrderId = builder.externalOrderId;
        this.orderNumber = builder.orderNumber;
        this.paidAt = builder.paidAt;
        this.currency = builder.currency;
        this.subtotalPrice = builder.subtotalPrice;
        this.discountTotal = builder.discountTotal;
        this.totalPrice = builder.totalPrice;
        this.customerEmailHash = builder.customerEmailHash;
        this.checkoutToken = builder.checkoutToken;
        this.purchaseIntentToken = builder.purchaseIntentToken;
    }

    public String getStoreId() { return storeId; }
    public String getExternalOrderId() { return externalOrderId; }
    public String getOrderNumber() { return orderNumber; }
    public String getPaidAt() { return paidAt; }
    public String getCurrency() { return currency; }
    public Double getSubtotalPrice() { return subtotalPrice; }
    public Double getDiscountTotal() { return discountTotal; }
    public Double getTotalPrice() { return totalPrice; }
    public String getCustomerEmailHash() { return customerEmailHash; }
    public String getCheckoutToken() { return checkoutToken; }
    public String getPurchaseIntentToken() { return purchaseIntentToken; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String storeId;
        private String externalOrderId;
        private String orderNumber;
        private String paidAt;
        private String currency;
        private Double subtotalPrice;
        private Double discountTotal;
        private Double totalPrice;
        private String customerEmailHash;
        private String checkoutToken;
        private String purchaseIntentToken;

        private Builder() {}

        /** Required. Store UUID where the order originated. */
        public Builder storeId(String storeId) { this.storeId = storeId; return this; }

        /** Required. Stable external order ID from your platform. */
        public Builder externalOrderId(String externalOrderId) { this.externalOrderId = externalOrderId; return this; }

        /** Optional human-readable order number. */
        public Builder orderNumber(String orderNumber) { this.orderNumber = orderNumber; return this; }

        /** Optional ISO-8601 datetime the order was paid. */
        public Builder paidAt(String paidAt) { this.paidAt = paidAt; return this; }

        /** Required. ISO-4217 currency code, e.g. {@code "USD"}. */
        public Builder currency(String currency) { this.currency = currency; return this; }

        /** Optional subtotal before discounts. */
        public Builder subtotalPrice(double subtotalPrice) { this.subtotalPrice = subtotalPrice; return this; }

        /** Optional total discount applied. */
        public Builder discountTotal(double discountTotal) { this.discountTotal = discountTotal; return this; }

        /** Optional total order amount (GMV). */
        public Builder totalPrice(double totalPrice) { this.totalPrice = totalPrice; return this; }

        /** Optional hashed customer email for duplicate detection. */
        public Builder customerEmailHash(String customerEmailHash) { this.customerEmailHash = customerEmailHash; return this; }

        /** Optional checkout token from your platform's checkout session. */
        public Builder checkoutToken(String checkoutToken) { this.checkoutToken = checkoutToken; return this; }

        /**
         * Optional Purchase Intent Token (PIT) locked at cart time.
         * Include this for order attribution to a creator's influence link.
         */
        public Builder purchaseIntentToken(String purchaseIntentToken) { this.purchaseIntentToken = purchaseIntentToken; return this; }

        public RecordPaidOrderRequest build() {
            if (storeId == null || storeId.trim().isEmpty())
                throw new QredexValidationException("RecordPaidOrderRequest requires storeId.");
            if (externalOrderId == null || externalOrderId.trim().isEmpty())
                throw new QredexValidationException("RecordPaidOrderRequest requires externalOrderId.");
            if (currency == null || currency.trim().isEmpty())
                throw new QredexValidationException("RecordPaidOrderRequest requires currency.");
            return new RecordPaidOrderRequest(this);
        }
    }
}
