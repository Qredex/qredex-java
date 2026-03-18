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
package com.qredex.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qredex.exceptions.QredexValidationException;
import com.qredex.model.standards.LinkStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Request body for creating a new Qredex influence link.
 *
 * <pre>{@code
 * CreateLinkRequest req = CreateLinkRequest.builder()
 *     .storeId("61abc354-dd8d-4a23-be02-ece77b1b4da6")
 *     .creatorId("16fca3f2-b346-4f98-8e52-0895aac61c5b")
 *     .linkName("spring-launch")
 *     .destinationPath("/products/spring-launch")
 *     .attributionWindowDays(30)
 *     .build();
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CreateLinkRequest {

    @JsonProperty("store_id")
    private final String storeId;

    @JsonProperty("creator_id")
    private final String creatorId;

    @JsonProperty("link_name")
    private final String linkName;

    @JsonProperty("destination_path")
    private final String destinationPath;

    @JsonProperty("note")
    private final String note;

    @JsonProperty("attribution_window_days")
    private final Integer attributionWindowDays;

    @JsonProperty("link_expiry_at")
    private final String linkExpiryAt;

    @JsonProperty("discount_code")
    private final String discountCode;

    @JsonProperty("status")
    private final LinkStatus status;

    private CreateLinkRequest(Builder builder) {
        this.storeId = builder.storeId;
        this.creatorId = builder.creatorId;
        this.linkName = builder.linkName;
        this.destinationPath = builder.destinationPath;
        this.note = builder.note;
        this.attributionWindowDays = builder.attributionWindowDays;
        this.linkExpiryAt = builder.linkExpiryAt;
        this.discountCode = builder.discountCode;
        this.status = builder.status;
    }

    @NotNull
    public String getStoreId() { return storeId; }
    @NotNull
    public String getCreatorId() { return creatorId; }
    @NotNull
    public String getLinkName() { return linkName; }
    @NotNull
    public String getDestinationPath() { return destinationPath; }
    @Nullable
    public String getNote() { return note; }
    @Nullable
    public Integer getAttributionWindowDays() { return attributionWindowDays; }
    @Nullable
    public String getLinkExpiryAt() { return linkExpiryAt; }
    @Nullable
    public String getDiscountCode() { return discountCode; }
    @Nullable
    public LinkStatus getStatus() { return status; }

    @NotNull
    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String storeId;
        private String creatorId;
        private String linkName;
        private String destinationPath;
        private String note;
        private Integer attributionWindowDays;
        private String linkExpiryAt;
        private String discountCode;
        private LinkStatus status;

        private Builder() {}

        /** Required. Store UUID to associate this link with. */
        @NotNull
        public Builder storeId(@NotNull String storeId) { this.storeId = storeId; return this; }

        /** Required. Creator UUID to associate this link with. */
        @NotNull
        public Builder creatorId(@NotNull String creatorId) { this.creatorId = creatorId; return this; }

        /** Required. Human-readable name for this link. */
        @NotNull
        public Builder linkName(@NotNull String linkName) { this.linkName = linkName; return this; }

        /** Required. Path on the merchant's store to redirect to. */
        @NotNull
        public Builder destinationPath(@NotNull String destinationPath) { this.destinationPath = destinationPath; return this; }

        /** Optional note visible only to merchants. */
        @NotNull
        public Builder note(@Nullable String note) { this.note = note; return this; }

        /** Optional attribution window in days. Server default applies when absent. */
        @NotNull
        public Builder attributionWindowDays(int attributionWindowDays) { this.attributionWindowDays = attributionWindowDays; return this; }

        /** Optional ISO-8601 datetime at which this link expires. */
        @NotNull
        public Builder linkExpiryAt(@Nullable String linkExpiryAt) { this.linkExpiryAt = linkExpiryAt; return this; }

        /** Optional discount code to attach to this link. */
        @NotNull
        public Builder discountCode(@Nullable String discountCode) { this.discountCode = discountCode; return this; }

        /** Optional initial status. Defaults to ACTIVE when absent. */
        @NotNull
        public Builder status(@Nullable LinkStatus status) { this.status = status; return this; }

        @NotNull
        public CreateLinkRequest build() {
            if (storeId == null || storeId.trim().isEmpty())
                throw new QredexValidationException("CreateLinkRequest requires storeId.");
            if (creatorId == null || creatorId.trim().isEmpty())
                throw new QredexValidationException("CreateLinkRequest requires creatorId.");
            if (linkName == null || linkName.trim().isEmpty())
                throw new QredexValidationException("CreateLinkRequest requires linkName.");
            if (destinationPath == null || destinationPath.trim().isEmpty())
                throw new QredexValidationException("CreateLinkRequest requires destinationPath.");
            return new CreateLinkRequest(this);
        }
    }

    @Override
    public String toString() {
        return "CreateLinkRequest{storeId='" + storeId + "', creatorId='" + creatorId
                + "', linkName='" + linkName + "', destinationPath='" + destinationPath + "'}";
    }
}
