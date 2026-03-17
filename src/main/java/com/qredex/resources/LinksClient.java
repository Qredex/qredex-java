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
package com.qredex.resources;

import com.qredex.exceptions.QredexValidationException;
import com.qredex.internal.HttpTransport;
import com.qredex.internal.QueryParams;
import com.qredex.internal.TokenProvider;
import com.qredex.model.request.CreateLinkRequest;
import com.qredex.model.request.ListLinksRequest;
import com.qredex.model.response.LinkPageResponse;
import com.qredex.model.response.LinkResponse;
import com.qredex.model.response.LinkStatsResponse;

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
    public LinkResponse create(CreateLinkRequest request) {
        return transport.post(
            "/api/v1/integrations/links",
            request,
            tokenProvider.getAuthorizationHeader(),
            LinkResponse.class);
    }

    /**
     * Retrieves a single influence link by its UUID.
     *
     * @param linkId the UUID of the link
     * @return the {@link LinkResponse}
     */
    public LinkResponse get(String linkId) {
        if (linkId == null || linkId.trim().isEmpty()) {
            throw new QredexValidationException("linkId must not be blank.");
        }
        return transport.get(
            "/api/v1/integrations/links/" + encode(linkId),
            null,
            tokenProvider.getAuthorizationHeader(),
            LinkResponse.class);
    }

    /**
     * Lists influence links with optional filters and pagination.
     *
     * @param request optional filters; use {@link ListLinksRequest#defaults()} for none
     * @return a paginated {@link LinkPageResponse}
     */
    public LinkPageResponse list(ListLinksRequest request) {
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
            LinkPageResponse.class);
    }

    /**
     * Retrieves aggregate click, session, and revenue statistics for a link.
     *
     * @param linkId the UUID of the link
     * @return the {@link LinkStatsResponse}
     */
    public LinkStatsResponse getStats(String linkId) {
        if (linkId == null || linkId.trim().isEmpty()) {
            throw new QredexValidationException("linkId must not be blank.");
        }
        return transport.get(
            "/api/v1/integrations/links/" + encode(linkId) + "/stats",
            null,
            tokenProvider.getAuthorizationHeader(),
            LinkStatsResponse.class);
    }

    private static String encode(String value) {
        try { return java.net.URLEncoder.encode(value, "UTF-8"); }
        catch (java.io.UnsupportedEncodingException e) { return value; }
    }
}
