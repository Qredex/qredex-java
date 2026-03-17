/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Paginated influence link list response. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class LinkPageResponse {

    @JsonProperty("items")
    private List<LinkListResponse> items;

    @JsonProperty("page")
    private int page;

    @JsonProperty("size")
    private int size;

    @JsonProperty("total_elements")
    private long totalElements;

    @JsonProperty("total_pages")
    private int totalPages;

    public List<LinkListResponse> getItems() { return items; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotalElements() { return totalElements; }
    public int getTotalPages() { return totalPages; }

    @Override
    public String toString() {
        return "LinkPageResponse{page=" + page + ", size=" + size + ", totalElements=" + totalElements + "}";
    }
}
