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
import com.qredex.model.standards.LinkStatus;

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

    @Override
    public String toString() {
        return "LinkResponse{id='" + id + "', linkName='" + linkName + "', linkCode='" + linkCode + "', status=" + status + "}";
    }
}
