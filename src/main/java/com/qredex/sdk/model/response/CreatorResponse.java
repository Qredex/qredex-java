/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qredex.sdk.model.enums.CreatorStatus;
import java.util.Map;

/** Full creator resource response. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class CreatorResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("handle")
    private String handle;

    @JsonProperty("status")
    private CreatorStatus status;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("socials")
    private Map<String, String> socials;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    public String getId() { return id; }
    public String getHandle() { return handle; }
    public CreatorStatus getStatus() { return status; }
    public String getDisplayName() { return displayName; }
    public String getEmail() { return email; }
    public Map<String, String> getSocials() { return socials; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}
