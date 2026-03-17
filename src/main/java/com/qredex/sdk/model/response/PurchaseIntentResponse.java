/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qredex.sdk.model.enums.OriginMatchStatus;
import com.qredex.sdk.model.enums.WindowStatus;

/**
 * Purchase Intent Token (PIT) response from the authenticated Integrations lock endpoint.
 *
 * <p>Contains full attribution details, snapshots, and eligibility signals.
 * Preserve {@link #getToken()} to submit with the paid order for attribution.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class PurchaseIntentResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("merchant_id")
    private String merchantId;

    @JsonProperty("store_id")
    private String storeId;

    @JsonProperty("link_id")
    private String linkId;

    @JsonProperty("influence_intent_id")
    private String influenceIntentId;

    @JsonProperty("token")
    private String token;

    @JsonProperty("token_id")
    private String tokenId;

    @JsonProperty("source")
    private String source;

    @JsonProperty("origin_match_status")
    private OriginMatchStatus originMatchStatus;

    @JsonProperty("window_status")
    private WindowStatus windowStatus;

    @JsonProperty("attribution_window_days")
    private Integer attributionWindowDays;

    @JsonProperty("attribution_window_days_snapshot")
    private Integer attributionWindowDaysSnapshot;

    @JsonProperty("store_domain_snapshot")
    private String storeDomainSnapshot;

    @JsonProperty("link_expiry_at_snapshot")
    private String linkExpiryAtSnapshot;

    @JsonProperty("discount_code_snapshot")
    private String discountCodeSnapshot;

    @JsonProperty("issued_at")
    private String issuedAt;

    @JsonProperty("expires_at")
    private String expiresAt;

    @JsonProperty("locked_at")
    private String lockedAt;

    @JsonProperty("integrity_version")
    private int integrityVersion;

    @JsonProperty("eligible")
    private Boolean eligible;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    public String getId() { return id; }
    public String getMerchantId() { return merchantId; }
    public String getStoreId() { return storeId; }
    public String getLinkId() { return linkId; }
    public String getInfluenceIntentId() { return influenceIntentId; }
    /** The PIT token string. Include this in {@code RecordPaidOrderRequest.purchaseIntentToken}. */
    public String getToken() { return token; }
    public String getTokenId() { return tokenId; }
    public String getSource() { return source; }
    public OriginMatchStatus getOriginMatchStatus() { return originMatchStatus; }
    public WindowStatus getWindowStatus() { return windowStatus; }
    public Integer getAttributionWindowDays() { return attributionWindowDays; }
    public Integer getAttributionWindowDaysSnapshot() { return attributionWindowDaysSnapshot; }
    public String getStoreDomainSnapshot() { return storeDomainSnapshot; }
    public String getLinkExpiryAtSnapshot() { return linkExpiryAtSnapshot; }
    public String getDiscountCodeSnapshot() { return discountCodeSnapshot; }
    public String getIssuedAt() { return issuedAt; }
    public String getExpiresAt() { return expiresAt; }
    public String getLockedAt() { return lockedAt; }
    public int getIntegrityVersion() { return integrityVersion; }
    public Boolean getEligible() { return eligible; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}
