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
import com.qredex.exceptions.QredexValidationException;
import com.qredex.internal.HttpTransport;
import com.qredex.internal.QueryParams;
import com.qredex.internal.TokenProvider;
import com.qredex.model.request.CreateLinkRequest;
import com.qredex.model.request.ListLinksRequest;
import com.qredex.model.response.LinkPageResponse;
import com.qredex.model.response.LinkResponse;
import com.qredex.model.response.LinkStatsResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Influence link resource operations for the Qredex Integrations API.
 *
 * <p>Obtain via {@code qredex.links()}.
 */
public final class LinksClient {

    private final HttpTransport transport;
    private final TokenProvider tokenProvider;

    public LinksClient(HttpTransport transport, TokenProvider tokenProvider) {
        this.transport = transport;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Creates a new trackable influence link.
     *
     * @param request link creation parameters
     * @return the created {@link LinkResponse}
     */
    @NotNull
    public LinkResponse create(@NotNull CreateLinkRequest request) {
        return create(request, null);
    }

    @NotNull
    public LinkResponse create(@NotNull CreateLinkRequest request, @Nullable QredexCallOptions options) {
        if (request == null) {
            throw new QredexValidationException("request must not be null.");
        }
        return transport.post(
            "/api/v1/integrations/links",
            request,
            tokenProvider.getAuthorizationHeader(),
            LinkResponse.class,
            options);
    }

    /**
     * Retrieves a single influence link by its UUID.
     *
     * @param linkId the UUID of the link
     * @return the {@link LinkResponse}
     */
    @NotNull
    public LinkResponse get(@Nullable String linkId) {
        return get(linkId, null);
    }

    @NotNull
    public LinkResponse get(@Nullable String linkId, @Nullable QredexCallOptions options) {
        if (linkId == null || linkId.trim().isEmpty()) {
            throw new QredexValidationException("linkId must not be blank.");
        }
        return transport.get(
            "/api/v1/integrations/links/" + encode(linkId),
            null,
            tokenProvider.getAuthorizationHeader(),
            LinkResponse.class,
            options);
    }

    /**
     * Lists influence links with optional filters and pagination.
     *
     * @param request optional filters; use {@link ListLinksRequest#defaults()} for none
     * @return a paginated {@link LinkPageResponse}
     */
    @NotNull
    public LinkPageResponse list() {
        return list(null, null);
    }

    @NotNull
    public LinkPageResponse list(@Nullable ListLinksRequest request) {
        return list(request, null);
    }

    @NotNull
    public LinkPageResponse list(@Nullable ListLinksRequest request, @Nullable QredexCallOptions options) {
        if (request == null) request = ListLinksRequest.defaults();
        return transport.get(
            "/api/v1/integrations/links",
            QueryParams.create()
                .add("page", request.getPage())
                .add("size", request.getSize())
                .add("status", request.getStatus() != null ? request.getStatus().getValue() : null)
                .add("destination", request.getDestination())
                .add("expired", request.getExpired())
                .build(),
            tokenProvider.getAuthorizationHeader(),
            LinkPageResponse.class,
            options);
    }

    /**
     * Retrieves aggregate click, session, and revenue statistics for a link.
     *
     * @param linkId the UUID of the link
     * @return the {@link LinkStatsResponse}
     */
    @NotNull
    public LinkStatsResponse getStats(@Nullable String linkId) {
        return getStats(linkId, null);
    }

    @NotNull
    public LinkStatsResponse getStats(@Nullable String linkId, @Nullable QredexCallOptions options) {
        if (linkId == null || linkId.trim().isEmpty()) {
            throw new QredexValidationException("linkId must not be blank.");
        }
        return transport.get(
            "/api/v1/integrations/links/" + encode(linkId) + "/stats",
            null,
            tokenProvider.getAuthorizationHeader(),
            LinkStatsResponse.class,
            options);
    }

    private static String encode(String value) {
        try { return java.net.URLEncoder.encode(value, "UTF-8"); }
        catch (java.io.UnsupportedEncodingException e) { return value; }
    }
}
