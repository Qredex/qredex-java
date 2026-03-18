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
import com.qredex.model.standards.*;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Detailed order attribution record including score breakdown and event timeline.
 * Returned by {@code orders().getDetails(orderAttributionId)}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class OrderAttributionDetailsResponse {

    @JsonProperty("id")
    private String id;

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

    @JsonProperty("attribution_locked_at")
    private String attributionLockedAt;

    @JsonProperty("attribution_window_days")
    private Integer attributionWindowDays;

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

    @JsonProperty("score_breakdown_json")
    private OrderAttributionScoreBreakdownResponse scoreBreakdown;

    @JsonProperty("resolution_status")
    private ResolutionStatus resolutionStatus;

    @JsonProperty("timeline")
    private List<OrderAttributionTimelineEventResponse> timeline;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @Nullable
    public String getId() { return id; }
    @Nullable
    public OrderSource getOrderSource() { return orderSource; }
    @Nullable
    public String getExternalOrderId() { return externalOrderId; }
    @Nullable
    public String getOrderNumber() { return orderNumber; }
    @Nullable
    public String getPaidAt() { return paidAt; }
    @Nullable
    public String getCurrency() { return currency; }
    @Nullable
    public Double getSubtotalPrice() { return subtotalPrice; }
    @Nullable
    public Double getDiscountTotal() { return discountTotal; }
    @Nullable
    public Double getTotalPrice() { return totalPrice; }
    @Nullable
    public String getLinkId() { return linkId; }
    @Nullable
    public String getLinkName() { return linkName; }
    @Nullable
    public String getLinkCode() { return linkCode; }
    @Nullable
    public String getCreatorId() { return creatorId; }
    @Nullable
    public String getCreatorHandle() { return creatorHandle; }
    @Nullable
    public String getCreatorDisplayName() { return creatorDisplayName; }
    public boolean isDuplicateSuspect() { return duplicateSuspect; }
    @Nullable
    public DuplicateConfidence getDuplicateConfidence() { return duplicateConfidence; }
    @Nullable
    public DuplicateReason getDuplicateReason() { return duplicateReason; }
    @Nullable
    public String getDuplicateOfOrderAttributionId() { return duplicateOfOrderAttributionId; }
    @Nullable
    public String getAttributionLockedAt() { return attributionLockedAt; }
    @Nullable
    public Integer getAttributionWindowDays() { return attributionWindowDays; }
    @Nullable
    public WindowStatus getWindowStatus() { return windowStatus; }
    @Nullable
    public TokenIntegrity getTokenIntegrity() { return tokenIntegrity; }
    @Nullable
    public IntegrityReason getIntegrityReason() { return integrityReason; }
    @Nullable
    public OriginMatchStatus getOriginMatchStatus() { return originMatchStatus; }
    public int getIntegrityScore() { return integrityScore; }
    @Nullable
    public IntegrityBand getIntegrityBand() { return integrityBand; }
    public boolean isReviewRequired() { return reviewRequired; }
    @Nullable
    public OrderAttributionScoreBreakdownResponse getScoreBreakdown() { return scoreBreakdown; }
    @Nullable
    public ResolutionStatus getResolutionStatus() { return resolutionStatus; }
    @Nullable
    public List<OrderAttributionTimelineEventResponse> getTimeline() { return timeline == null ? null : Collections.unmodifiableList(timeline); }
    @Nullable
    public String getCreatedAt() { return createdAt; }
    @Nullable
    public String getUpdatedAt() { return updatedAt; }

    @Override
    public String toString() {
        return "OrderAttributionDetailsResponse{id='" + id + "', externalOrderId='" + externalOrderId
                + "', resolutionStatus=" + resolutionStatus + ", integrityScore=" + integrityScore
                + ", integrityBand=" + integrityBand + "}";
    }
}
