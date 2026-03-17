/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qredex.sdk.model.enums.LinkStatus;

/** Full influence link resource response. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class LinkResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("merchant_id")
    private String merchantId;

    @JsonProperty("store_id")
    private String storeId;

    @JsonProperty("creator_id")
    private String creatorId;

    @JsonProperty("link_name")
    private String linkName;

    @JsonProperty("link_code")
    private String linkCode;

    @JsonProperty("public_link_url")
    private String publicLinkUrl;

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

    @JsonProperty("disabled_at")
    private String disabledAt;

    @JsonProperty("discount_code")
    private String discountCode;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    public String getId() { return id; }
    public String getMerchantId() { return merchantId; }
    public String getStoreId() { return storeId; }
    public String getCreatorId() { return creatorId; }
    public String getLinkName() { return linkName; }
    public String getLinkCode() { return linkCode; }
    public String getPublicLinkUrl() { return publicLinkUrl; }
    public String getDestinationPath() { return destinationPath; }
    public String getNote() { return note; }
    public LinkStatus getStatus() { return status; }
    public int getAttributionWindowDays() { return attributionWindowDays; }
    public String getLinkExpiryAt() { return linkExpiryAt; }
    public String getDisabledAt() { return disabledAt; }
    public String getDiscountCode() { return discountCode; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}
