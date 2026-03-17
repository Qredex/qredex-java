/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.model.request;

/**
 * Query parameters for listing order attribution records with optional pagination.
 */
public final class ListOrdersRequest {

    private final Integer page;
    private final Integer size;

    private ListOrdersRequest(Builder builder) {
        this.page = builder.page;
        this.size = builder.size;
    }

    public Integer getPage() { return page; }
    public Integer getSize() { return size; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Integer page;
        private Integer size;

        private Builder() {}

        public Builder page(int page) { this.page = page; return this; }
        public Builder size(int size) { this.size = size; return this; }

        public ListOrdersRequest build() { return new ListOrdersRequest(this); }
    }

    /** Returns an empty request (no filters, server defaults). */
    public static ListOrdersRequest defaults() { return new Builder().build(); }

    @Override
    public String toString() {
        return "ListOrdersRequest{page=" + page + ", size=" + size + "}";
    }
}
