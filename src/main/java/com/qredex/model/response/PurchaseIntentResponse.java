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
package com.qredex.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qredex.model.standards.OriginMatchStatus;
import com.qredex.model.standards.WindowStatus;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    public String getId() { return id; }
    @Nullable
    public String getMerchantId() { return merchantId; }
    @Nullable
    public String getStoreId() { return storeId; }
    @Nullable
    public String getLinkId() { return linkId; }
    @Nullable
    public String getInfluenceIntentId() { return influenceIntentId; }
    /** The PIT token string. Include this in {@code RecordPaidOrderRequest.purchaseIntentToken}. */
    @Nullable
    public String getToken() { return token; }
    @Nullable
    public String getTokenId() { return tokenId; }
    @Nullable
    public String getSource() { return source; }
    @Nullable
    public OriginMatchStatus getOriginMatchStatus() { return originMatchStatus; }
    @Nullable
    public WindowStatus getWindowStatus() { return windowStatus; }
    @Nullable
    public Integer getAttributionWindowDays() { return attributionWindowDays; }
    @Nullable
    public Integer getAttributionWindowDaysSnapshot() { return attributionWindowDaysSnapshot; }
    @Nullable
    public String getStoreDomainSnapshot() { return storeDomainSnapshot; }
    @Nullable
    public String getLinkExpiryAtSnapshot() { return linkExpiryAtSnapshot; }
    @Nullable
    public String getDiscountCodeSnapshot() { return discountCodeSnapshot; }
    @Nullable
    public String getIssuedAt() { return issuedAt; }
    @Nullable
    public String getExpiresAt() { return expiresAt; }
    @Nullable
    public String getLockedAt() { return lockedAt; }
    public int getIntegrityVersion() { return integrityVersion; }
    @Nullable
    public Boolean getEligible() { return eligible; }
    @Nullable
    public String getCreatedAt() { return createdAt; }
    @Nullable
    public String getUpdatedAt() { return updatedAt; }

    @Override
    public String toString() {
        return "PurchaseIntentResponse{id='" + id + "', tokenId='" + tokenId
                + "', eligible=" + eligible + ", windowStatus=" + windowStatus + "}";
    }
}
