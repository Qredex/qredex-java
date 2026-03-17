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

/** Influence Intent Token (IIT) issued via the Integrations API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class InfluenceIntentResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("merchant_id")
    private String merchantId;

    @JsonProperty("link_id")
    private String linkId;

    @JsonProperty("token")
    private String token;

    @JsonProperty("token_id")
    private String tokenId;

    @JsonProperty("issued_at")
    private String issuedAt;

    @JsonProperty("expires_at")
    private String expiresAt;

    @JsonProperty("status")
    private String status;

    @JsonProperty("integrity_version")
    private int integrityVersion;

    @JsonProperty("ip_hash")
    private String ipHash;

    @JsonProperty("user_agent_hash")
    private String userAgentHash;

    @JsonProperty("referrer")
    private String referrer;

    @JsonProperty("landing_path")
    private String landingPath;

    public String getId() { return id; }
    public String getMerchantId() { return merchantId; }
    public String getLinkId() { return linkId; }
    /** The IIT token string. Pass this to the browser agent or directly to {@code lockPurchaseIntent}. */
    public String getToken() { return token; }
    public String getTokenId() { return tokenId; }
    public String getIssuedAt() { return issuedAt; }
    public String getExpiresAt() { return expiresAt; }
    public String getStatus() { return status; }
    public int getIntegrityVersion() { return integrityVersion; }
    public String getIpHash() { return ipHash; }
    public String getUserAgentHash() { return userAgentHash; }
    public String getReferrer() { return referrer; }
    public String getLandingPath() { return landingPath; }

    @Override
    public String toString() {
        return "InfluenceIntentResponse{id='" + id + "', linkId='" + linkId + "', tokenId='" + tokenId + "'}";
    }
}
