/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** OAuth2 client-credentials token response from {@code /api/v1/auth/token}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class OAuthTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("scope")
    private String scope;

    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }
    public Long getExpiresIn() { return expiresIn; }
    public String getScope() { return scope; }

    @Override
    public String toString() {
        return "OAuthTokenResponse{tokenType='" + tokenType + "', expiresIn=" + expiresIn + ", scope='" + scope + "'}";
    }
}
