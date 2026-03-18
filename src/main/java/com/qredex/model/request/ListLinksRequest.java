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

import com.qredex.model.standards.LinkStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Query parameters for listing influence links with optional filters and pagination.
 */
public final class ListLinksRequest {

    private final Integer page;
    private final Integer size;
    private final LinkStatus status;
    private final String destination;
    private final Boolean expired;

    private ListLinksRequest(Builder builder) {
        this.page = builder.page;
        this.size = builder.size;
        this.status = builder.status;
        this.destination = builder.destination;
        this.expired = builder.expired;
    }

    @Nullable
    public Integer getPage() { return page; }
    @Nullable
    public Integer getSize() { return size; }
    @Nullable
    public LinkStatus getStatus() { return status; }
    @Nullable
    public String getDestination() { return destination; }
    @Nullable
    public Boolean getExpired() { return expired; }

    @NotNull
    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Integer page;
        private Integer size;
        private LinkStatus status;
        private String destination;
        private Boolean expired;

        private Builder() {}

        @NotNull
        public Builder page(int page) { this.page = page; return this; }
        @NotNull
        public Builder size(int size) { this.size = size; return this; }
        @NotNull
        public Builder status(@Nullable LinkStatus status) { this.status = status; return this; }
        @NotNull
        public Builder destination(@Nullable String destination) { this.destination = destination; return this; }
        @NotNull
        public Builder expired(boolean expired) { this.expired = expired; return this; }

        @NotNull
        public ListLinksRequest build() { return new ListLinksRequest(this); }
    }

    /** Returns an empty request (no filters, server defaults). */
    @NotNull
    public static ListLinksRequest defaults() { return new Builder().build(); }

    @Override
    public String toString() {
        return "ListLinksRequest{page=" + page + ", size=" + size + ", status=" + status + "}";
    }
}
