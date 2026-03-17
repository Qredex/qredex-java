/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.model.request;

import com.qredex.model.standards.CreatorStatus;

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

    public Integer getPage() { return page; }
    public Integer getSize() { return size; }
    public CreatorStatus getStatus() { return status; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Integer page;
        private Integer size;
        private CreatorStatus status;

        private Builder() {}

        public Builder page(int page) { this.page = page; return this; }
        public Builder size(int size) { this.size = size; return this; }
        public Builder status(CreatorStatus status) { this.status = status; return this; }

        public ListCreatorsRequest build() { return new ListCreatorsRequest(this); }
    }

    /** Returns an empty request (no filters, server defaults). */
    public static ListCreatorsRequest defaults() { return new Builder().build(); }

    @Override
    public String toString() {
        return "ListCreatorsRequest{page=" + page + ", size=" + size + ", status=" + status + "}";
    }
}
