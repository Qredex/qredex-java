/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Request body for creating a new Qredex creator.
 *
 * <p>Use the {@link Builder} to construct instances:
 * <pre>{@code
 * CreateCreatorRequest req = CreateCreatorRequest.builder()
 *     .handle("alice")
 *     .displayName("Alice")
 *     .email("alice@example.com")
 *     .build();
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CreateCreatorRequest {

    @JsonProperty("handle")
    private final String handle;

    @JsonProperty("display_name")
    private final String displayName;

    @JsonProperty("email")
    private final String email;

    @JsonProperty("socials")
    private final Map<String, String> socials;

    private CreateCreatorRequest(Builder builder) {
        this.handle = builder.handle;
        this.displayName = builder.displayName;
        this.email = builder.email;
        this.socials = builder.socials;
    }

    public String getHandle() { return handle; }
    public String getDisplayName() { return displayName; }
    public String getEmail() { return email; }
    public Map<String, String> getSocials() { return socials; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String handle;
        private String displayName;
        private String email;
        private Map<String, String> socials;

        private Builder() {}

        /** Required. Unique creator handle (slug). */
        public Builder handle(String handle) { this.handle = handle; return this; }

        /** Optional display name shown to merchants. */
        public Builder displayName(String displayName) { this.displayName = displayName; return this; }

        /** Optional creator email address. */
        public Builder email(String email) { this.email = email; return this; }

        /** Optional social profile map, e.g. {@code {"instagram": "alice"}}. */
        public Builder socials(Map<String, String> socials) { this.socials = socials; return this; }

        public CreateCreatorRequest build() {
            if (handle == null || handle.trim().isEmpty()) {
                throw new IllegalStateException("CreateCreatorRequest requires a non-blank handle.");
            }
            return new CreateCreatorRequest(this);
        }
    }
}
