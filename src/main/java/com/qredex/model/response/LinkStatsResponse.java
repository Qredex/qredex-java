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
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    public String getLinkId() { return linkId; }
    @Nullable
    public Long getClicksCount() { return clicksCount; }
    @Nullable
    public Long getSessionsCount() { return sessionsCount; }
    @Nullable
    public Long getOrdersCount() { return ordersCount; }
    @Nullable
    public Double getRevenueTotal() { return revenueTotal; }
    @Nullable
    public Long getTokenInvalidCount() { return tokenInvalidCount; }
    @Nullable
    public Long getTokenMissingCount() { return tokenMissingCount; }
    @Nullable
    public String getLastClickAt() { return lastClickAt; }
    @Nullable
    public String getLastOrderAt() { return lastOrderAt; }

    @Override
    public String toString() {
        return "LinkStatsResponse{linkId='" + linkId + "', clicksCount=" + clicksCount + ", ordersCount=" + ordersCount + "}";
    }
}
