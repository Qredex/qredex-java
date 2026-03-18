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
package com.qredex.resources;

import com.qredex.QredexCallOptions;
import com.qredex.internal.HttpTransport;
import com.qredex.internal.TokenProvider;
import com.qredex.model.request.IssueInfluenceIntentTokenRequest;
import com.qredex.model.request.LockPurchaseIntentRequest;
import com.qredex.model.response.InfluenceIntentResponse;
import com.qredex.model.response.PurchaseIntentResponse;
import com.qredex.exceptions.QredexValidationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @NotNull
    public InfluenceIntentResponse issueInfluenceIntentToken(@NotNull IssueInfluenceIntentTokenRequest request) {
        return issueInfluenceIntentToken(request, null);
    }

    @NotNull
    public InfluenceIntentResponse issueInfluenceIntentToken(
        @NotNull IssueInfluenceIntentTokenRequest request,
        @Nullable QredexCallOptions options
    ) {
        if (request == null) {
            throw new QredexValidationException("request must not be null.");
        }
        return transport.post(
            "/api/v1/integrations/intents/token",
            request,
            tokenProvider.getAuthorizationHeader(),
            InfluenceIntentResponse.class,
            options);
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
    @NotNull
    public PurchaseIntentResponse lockPurchaseIntent(@NotNull LockPurchaseIntentRequest request) {
        return lockPurchaseIntent(request, null);
    }

    @NotNull
    public PurchaseIntentResponse lockPurchaseIntent(
        @NotNull LockPurchaseIntentRequest request,
        @Nullable QredexCallOptions options
    ) {
        if (request == null) {
            throw new QredexValidationException("request must not be null.");
        }
        return transport.post(
            "/api/v1/integrations/intents/lock",
            request,
            tokenProvider.getAuthorizationHeader(),
            PurchaseIntentResponse.class,
            options);
    }
}
