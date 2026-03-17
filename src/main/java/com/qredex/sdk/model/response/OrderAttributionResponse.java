/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qredex.sdk.model.standards.*;

/**
 * Order attribution record returned after a paid order is recorded or when listing orders.
 *
 * <p>Key fields for integration correctness:
 * <ul>
 *   <li>{@link #getResolutionStatus()} — ATTRIBUTED / UNATTRIBUTED / REJECTED</li>
 *   <li>{@link #getTokenIntegrity()} — VALID / INVALID</li>
 *   <li>{@link #getIntegrityReason()} — reason for INVALID verdict</li>
 *   <li>{@link #getIntegrityScore()} — 0–100 confidence score</li>
 * </ul>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class OrderAttributionResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("merchant_id")
    private String merchantId;

    @JsonProperty("order_source")
    private OrderSource orderSource;

    @JsonProperty("external_order_id")
    private String externalOrderId;

    @JsonProperty("order_number")
    private String orderNumber;

    @JsonProperty("paid_at")
    private String paidAt;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("subtotal_price")
    private Double subtotalPrice;

    @JsonProperty("discount_total")
    private Double discountTotal;

    @JsonProperty("total_price")
    private Double totalPrice;

    @JsonProperty("purchase_intent_token")
    private String purchaseIntentToken;

    @JsonProperty("link_id")
    private String linkId;

    @JsonProperty("link_name")
    private String linkName;

    @JsonProperty("link_code")
    private String linkCode;

    @JsonProperty("creator_id")
    private String creatorId;

    @JsonProperty("creator_handle")
    private String creatorHandle;

    @JsonProperty("creator_display_name")
    private String creatorDisplayName;

    @JsonProperty("duplicate_suspect")
    private boolean duplicateSuspect;

    @JsonProperty("duplicate_confidence")
    private DuplicateConfidence duplicateConfidence;

    @JsonProperty("duplicate_reason")
    private DuplicateReason duplicateReason;

    @JsonProperty("duplicate_of_order_attribution_id")
    private String duplicateOfOrderAttributionId;

    @JsonProperty("window_status")
    private WindowStatus windowStatus;

    @JsonProperty("token_integrity")
    private TokenIntegrity tokenIntegrity;

    @JsonProperty("integrity_reason")
    private IntegrityReason integrityReason;

    @JsonProperty("origin_match_status")
    private OriginMatchStatus originMatchStatus;

    @JsonProperty("integrity_score")
    private int integrityScore;

    @JsonProperty("integrity_band")
    private IntegrityBand integrityBand;

    @JsonProperty("review_required")
    private boolean reviewRequired;

    @JsonProperty("resolution_status")
    private ResolutionStatus resolutionStatus;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    public String getId() { return id; }
    public String getMerchantId() { return merchantId; }
    public OrderSource getOrderSource() { return orderSource; }
    public String getExternalOrderId() { return externalOrderId; }
    public String getOrderNumber() { return orderNumber; }
    public String getPaidAt() { return paidAt; }
    public String getCurrency() { return currency; }
    public Double getSubtotalPrice() { return subtotalPrice; }
    public Double getDiscountTotal() { return discountTotal; }
    public Double getTotalPrice() { return totalPrice; }
    public String getPurchaseIntentToken() { return purchaseIntentToken; }
    public String getLinkId() { return linkId; }
    public String getLinkName() { return linkName; }
    public String getLinkCode() { return linkCode; }
    public String getCreatorId() { return creatorId; }
    public String getCreatorHandle() { return creatorHandle; }
    public String getCreatorDisplayName() { return creatorDisplayName; }
    public boolean isDuplicateSuspect() { return duplicateSuspect; }
    public DuplicateConfidence getDuplicateConfidence() { return duplicateConfidence; }
    public DuplicateReason getDuplicateReason() { return duplicateReason; }
    public String getDuplicateOfOrderAttributionId() { return duplicateOfOrderAttributionId; }
    public WindowStatus getWindowStatus() { return windowStatus; }
    public TokenIntegrity getTokenIntegrity() { return tokenIntegrity; }
    public IntegrityReason getIntegrityReason() { return integrityReason; }
    public OriginMatchStatus getOriginMatchStatus() { return originMatchStatus; }
    public int getIntegrityScore() { return integrityScore; }
    public IntegrityBand getIntegrityBand() { return integrityBand; }
    public boolean isReviewRequired() { return reviewRequired; }
    public ResolutionStatus getResolutionStatus() { return resolutionStatus; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    @Override
    public String toString() {
        return "OrderAttributionResponse{id='" + id + "', externalOrderId='" + externalOrderId
                + "', resolutionStatus=" + resolutionStatus + ", integrityBand=" + integrityBand + "}";
    }
}
