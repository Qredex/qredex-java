/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Aggregate click, session, and revenue statistics for an influence link. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class LinkStatsResponse {

    @JsonProperty("link_id")
    private String linkId;

    @JsonProperty("clicks_count")
    private Long clicksCount;

    @JsonProperty("sessions_count")
    private Long sessionsCount;

    @JsonProperty("orders_count")
    private Long ordersCount;

    @JsonProperty("revenue_total")
    private Double revenueTotal;

    @JsonProperty("token_invalid_count")
    private Long tokenInvalidCount;

    @JsonProperty("token_missing_count")
    private Long tokenMissingCount;

    @JsonProperty("last_click_at")
    private String lastClickAt;

    @JsonProperty("last_order_at")
    private String lastOrderAt;

    public String getLinkId() { return linkId; }
    public Long getClicksCount() { return clicksCount; }
    public Long getSessionsCount() { return sessionsCount; }
    public Long getOrdersCount() { return ordersCount; }
    public Double getRevenueTotal() { return revenueTotal; }
    public Long getTokenInvalidCount() { return tokenInvalidCount; }
    public Long getTokenMissingCount() { return tokenMissingCount; }
    public String getLastClickAt() { return lastClickAt; }
    public String getLastOrderAt() { return lastOrderAt; }
}
