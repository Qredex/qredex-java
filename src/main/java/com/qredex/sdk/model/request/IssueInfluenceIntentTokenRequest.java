/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qredex.sdk.exceptions.QredexValidationException;

/**
 * Request body for issuing an Influence Intent Token (IIT) via the Integrations API.
 *
 * <p>IIT issuance is the first step in the canonical attribution flow for backend
 * click event processing. The {@code link_id} is required.
 *
 * <pre>{@code
 * IssueInfluenceIntentTokenRequest req = IssueInfluenceIntentTokenRequest.builder()
 *     .linkId("16fca3f2-b346-4f98-8e52-0895aac61c5b")
 *     .referrer("https://instagram.com/alice")
 *     .landingPath("/products/spring-launch")
 *     .build();
 * }</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class IssueInfluenceIntentTokenRequest {

    @JsonProperty("link_id")
    private final String linkId;

    @JsonProperty("ip_hash")
    private final String ipHash;

    @JsonProperty("user_agent_hash")
    private final String userAgentHash;

    @JsonProperty("referrer")
    private final String referrer;

    @JsonProperty("landing_path")
    private final String landingPath;

    @JsonProperty("expires_at")
    private final String expiresAt;

    @JsonProperty("integrity_version")
    private final Integer integrityVersion;

    private IssueInfluenceIntentTokenRequest(Builder builder) {
        this.linkId = builder.linkId;
        this.ipHash = builder.ipHash;
        this.userAgentHash = builder.userAgentHash;
        this.referrer = builder.referrer;
        this.landingPath = builder.landingPath;
        this.expiresAt = builder.expiresAt;
        this.integrityVersion = builder.integrityVersion;
    }

    public String getLinkId() { return linkId; }
    public String getIpHash() { return ipHash; }
    public String getUserAgentHash() { return userAgentHash; }
    public String getReferrer() { return referrer; }
    public String getLandingPath() { return landingPath; }
    public String getExpiresAt() { return expiresAt; }
    public Integer getIntegrityVersion() { return integrityVersion; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String linkId;
        private String ipHash;
        private String userAgentHash;
        private String referrer;
        private String landingPath;
        private String expiresAt;
        private Integer integrityVersion;

        private Builder() {}

        /** Required. UUID of the influence link to issue the IIT for. */
        public Builder linkId(String linkId) { this.linkId = linkId; return this; }

        /** Optional hashed IP address of the clicking user. */
        public Builder ipHash(String ipHash) { this.ipHash = ipHash; return this; }

        /** Optional hashed user-agent string of the clicking user. */
        public Builder userAgentHash(String userAgentHash) { this.userAgentHash = userAgentHash; return this; }

        /** Optional referrer URL where the click originated. */
        public Builder referrer(String referrer) { this.referrer = referrer; return this; }

        /** Optional destination path where the user landed. */
        public Builder landingPath(String landingPath) { this.landingPath = landingPath; return this; }

        /** Optional ISO-8601 expiry override for this IIT. */
        public Builder expiresAt(String expiresAt) { this.expiresAt = expiresAt; return this; }

        /** Optional integrity version to use for token signing. */
        public Builder integrityVersion(int integrityVersion) { this.integrityVersion = integrityVersion; return this; }

        public IssueInfluenceIntentTokenRequest build() {
            if (linkId == null || linkId.trim().isEmpty())
                throw new QredexValidationException("IssueInfluenceIntentTokenRequest requires linkId.");
            return new IssueInfluenceIntentTokenRequest(this);
        }
    }
}
