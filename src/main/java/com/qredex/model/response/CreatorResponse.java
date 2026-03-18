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
import com.qredex.model.standards.CreatorStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
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

    @Nullable
    public String getId() { return id; }
    @Nullable
    public String getHandle() { return handle; }
    @Nullable
    public CreatorStatus getStatus() { return status; }
    @Nullable
    public String getDisplayName() { return displayName; }
    @Nullable
    public String getEmail() { return email; }
    @Nullable
    public Map<String, String> getSocials() { return socials == null ? null : Collections.unmodifiableMap(socials); }
    @Nullable
    public String getCreatedAt() { return createdAt; }
    @Nullable
    public String getUpdatedAt() { return updatedAt; }

    @Override
    public String toString() {
        return "CreatorResponse{id='" + id + "', handle='" + handle + "', status=" + status + "}";
    }
}
