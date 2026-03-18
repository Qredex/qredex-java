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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
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
        this.socials = builder.socials == null
            ? null
            : Collections.unmodifiableMap(new LinkedHashMap<String, String>(builder.socials));
    }

    @NotNull
    public String getHandle() { return handle; }
    @Nullable
    public String getDisplayName() { return displayName; }
    @Nullable
    public String getEmail() { return email; }
    @Nullable
    public Map<String, String> getSocials() { return socials; }

    @NotNull
    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String handle;
        private String displayName;
        private String email;
        private Map<String, String> socials;

        private Builder() {}

        /** Required. Unique creator handle (slug). */
        @NotNull
        public Builder handle(@NotNull String handle) { this.handle = handle; return this; }

        /** Optional display name shown to merchants. */
        @NotNull
        public Builder displayName(@Nullable String displayName) { this.displayName = displayName; return this; }

        /** Optional creator email address. */
        @NotNull
        public Builder email(@Nullable String email) { this.email = email; return this; }

        /** Optional social profile map, e.g. {@code {"instagram": "alice"}}. */
        @NotNull
        public Builder socials(@Nullable Map<String, String> socials) { this.socials = socials; return this; }

        @NotNull
        public CreateCreatorRequest build() {
            if (handle == null || handle.trim().isEmpty()) {
                throw new QredexValidationException("CreateCreatorRequest requires a non-blank handle.");
            }
            return new CreateCreatorRequest(this);
        }
    }

    @Override
    public String toString() {
        return "CreateCreatorRequest{handle='" + handle + "', displayName='" + displayName + "'}";
    }
}
