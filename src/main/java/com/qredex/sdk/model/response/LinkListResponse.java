/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qredex.sdk.model.enums.LinkStatus;

/** Link entry in a paginated list response, including aggregate stats. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class LinkListResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("store_id")
    private String storeId;

    @JsonProperty("link_name")
    private String linkName;

    @JsonProperty("link_code")
    private String linkCode;

    @JsonProperty("destination_path")
    private String destinationPath;

    @JsonProperty("note")
    private String note;

    @JsonProperty("status")
    private LinkStatus status;

    @JsonProperty("attribution_window_days")
    private int attributionWindowDays;

    @JsonProperty("link_expiry_at")
    private String linkExpiryAt;

    @JsonProperty("discount_code")
    private String discountCode;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("creator_id")
    private String creatorId;

    @JsonProperty("creator_handle")
    private String creatorHandle;

    @JsonProperty("creator_display_name")
    private String creatorDisplayName;

    @JsonProperty("clicks_count")
    private Long clicksCount;

    @JsonProperty("orders_count")
    private Long ordersCount;

    @JsonProperty("revenue_total")
    private Double revenueTotal;

    public String getId() { return id; }
    public String getStoreId() { return storeId; }
    public String getLinkName() { return linkName; }
    public String getLinkCode() { return linkCode; }
    public String getDestinationPath() { return destinationPath; }
    public String getNote() { return note; }
    public LinkStatus getStatus() { return status; }
    public int getAttributionWindowDays() { return attributionWindowDays; }
    public String getLinkExpiryAt() { return linkExpiryAt; }
    public String getDiscountCode() { return discountCode; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getCreatorId() { return creatorId; }
    public String getCreatorHandle() { return creatorHandle; }
    public String getCreatorDisplayName() { return creatorDisplayName; }
    public Long getClicksCount() { return clicksCount; }
    public Long getOrdersCount() { return ordersCount; }
    public Double getRevenueTotal() { return revenueTotal; }

    @Override
    public String toString() {
        return "LinkListResponse{id='" + id + "', linkName='" + linkName + "', linkCode='" + linkCode + "', status=" + status + "}";
    }
}
