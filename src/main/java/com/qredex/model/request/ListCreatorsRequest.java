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

import com.qredex.model.standards.CreatorStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Query parameters for listing creators with optional pagination and status filter.
 *
 * <pre>{@code
 * ListCreatorsRequest req = ListCreatorsRequest.builder()
 *     .page(0).size(20).status(CreatorStatus.ACTIVE).build();
 * }</pre>
 */
public final class ListCreatorsRequest {

    private final Integer page;
    private final Integer size;
    private final CreatorStatus status;

    private ListCreatorsRequest(Builder builder) {
        this.page = builder.page;
        this.size = builder.size;
        this.status = builder.status;
    }

    @Nullable
    public Integer getPage() { return page; }
    @Nullable
    public Integer getSize() { return size; }
    @Nullable
    public CreatorStatus getStatus() { return status; }

    @NotNull
    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Integer page;
        private Integer size;
        private CreatorStatus status;

        private Builder() {}

        @NotNull
        public Builder page(int page) { this.page = page; return this; }
        @NotNull
        public Builder size(int size) { this.size = size; return this; }
        @NotNull
        public Builder status(@Nullable CreatorStatus status) { this.status = status; return this; }

        @NotNull
        public ListCreatorsRequest build() { return new ListCreatorsRequest(this); }
    }

    /** Returns an empty request (no filters, server defaults). */
    @NotNull
    public static ListCreatorsRequest defaults() { return new Builder().build(); }

    @Override
    public String toString() {
        return "ListCreatorsRequest{page=" + page + ", size=" + size + ", status=" + status + "}";
    }
}
