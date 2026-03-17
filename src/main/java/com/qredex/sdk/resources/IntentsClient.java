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
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.resources;

import com.qredex.sdk.exceptions.QredexValidationException;
import com.qredex.sdk.internal.HttpTransport;
import com.qredex.sdk.internal.TokenProvider;
import com.qredex.sdk.model.request.IssueInfluenceIntentTokenRequest;
import com.qredex.sdk.model.request.LockPurchaseIntentRequest;
import com.qredex.sdk.model.response.InfluenceIntentResponse;
import com.qredex.sdk.model.response.PurchaseIntentResponse;

/**
 * Influence Intent Token (IIT) and Purchase Intent Token (PIT) operations
 * for the Qredex Integrations API.
 *
 * <p>Obtain via {@code qredex.intents()}.
 *
 * <h3>Canonical flow</h3>
 * <ol>
 *   <li>Call {@link #issueInfluenceIntentToken} to generate an IIT for a click event.</li>
 *   <li>At cart/add-to-cart time, call {@link #lockPurchaseIntent} with the IIT token.</li>
 *   <li>Submit the PIT with the paid order via {@code orders().recordPaidOrder(...)}.</li>
 * </ol>
 */
public final class IntentsClient {

    private final HttpTransport transport;
    private final TokenProvider tokenProvider;

    public IntentsClient(HttpTransport transport, TokenProvider tokenProvider) {
        this.transport = transport;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Issues an Influence Intent Token (IIT) for an authenticated backend click flow.
     *
     * <p>Use this when your backend processes click events and needs to create an IIT
     * programmatically (not via the public redirect link).
     *
     * @param request IIT parameters; {@code linkId} is required
     * @return the {@link InfluenceIntentResponse} containing the IIT {@code token}
     */
    public InfluenceIntentResponse issueInfluenceIntentToken(IssueInfluenceIntentTokenRequest request) {
        if (request == null) throw new QredexValidationException("IssueInfluenceIntentTokenRequest must not be null.");
        if (request.getLinkId() == null || request.getLinkId().trim().isEmpty()) {
            throw new QredexValidationException("IssueInfluenceIntentTokenRequest requires linkId.");
        }
        return transport.post(
            "/api/v1/integrations/intents/token",
            request,
            tokenProvider.getAuthorizationHeader(),
            InfluenceIntentResponse.class);
    }

    /**
     * Locks a Purchase Intent Token (PIT) via the authenticated machine-to-machine endpoint.
     *
     * <p>Returns the full {@link PurchaseIntentResponse} with complete attribution details,
     * snapshots, and eligibility signals. Preserve {@code response.getToken()} and pass it
     * to {@code orders().recordPaidOrder(...)} as {@code purchaseIntentToken}.
     *
     * @param request PIT lock parameters; the IIT {@code token} is required
     * @return the {@link PurchaseIntentResponse}
     */
    public PurchaseIntentResponse lockPurchaseIntent(LockPurchaseIntentRequest request) {
        if (request == null) throw new QredexValidationException("LockPurchaseIntentRequest must not be null.");
        if (request.getToken() == null || request.getToken().trim().isEmpty()) {
            throw new QredexValidationException("LockPurchaseIntentRequest requires a token.");
        }
        return transport.post(
            "/api/v1/integrations/intents/lock",
            request,
            tokenProvider.getAuthorizationHeader(),
            PurchaseIntentResponse.class);
    }
}
