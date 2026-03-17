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
import com.qredex.sdk.internal.QueryParams;
import com.qredex.sdk.internal.TokenProvider;
import com.qredex.sdk.model.request.CreateCreatorRequest;
import com.qredex.sdk.model.request.ListCreatorsRequest;
import com.qredex.sdk.model.response.CreatorPageResponse;
import com.qredex.sdk.model.response.CreatorResponse;

/**
 * Creator resource operations for the Qredex Integrations API.
 *
 * <p>Obtain via {@code qredex.creators()}.
 */
public final class CreatorsClient {

    private final HttpTransport transport;
    private final TokenProvider tokenProvider;

    public CreatorsClient(HttpTransport transport, TokenProvider tokenProvider) {
        this.transport = transport;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Creates a new creator record.
     *
     * @param request the creator to create; {@code handle} is required
     * @return the created {@link CreatorResponse}
     * @throws QredexValidationException if the request is invalid
     */
    public CreatorResponse create(CreateCreatorRequest request) {
        return transport.post(
            "/api/v1/integrations/creators",
            request,
            tokenProvider.getAuthorizationHeader(),
            CreatorResponse.class);
    }

    /**
     * Retrieves a single creator by their UUID.
     *
     * @param creatorId the UUID of the creator
     * @return the {@link CreatorResponse}
     * @throws QredexValidationException if {@code creatorId} is blank
     */
    public CreatorResponse get(String creatorId) {
        if (creatorId == null || creatorId.trim().isEmpty()) {
            throw new QredexValidationException("creatorId must not be blank.");
        }
        return transport.get(
            "/api/v1/integrations/creators/" + encode(creatorId),
            null,
            tokenProvider.getAuthorizationHeader(),
            CreatorResponse.class);
    }

    /**
     * Lists creators with optional pagination and status filter.
     *
     * @param request optional filters; use {@link ListCreatorsRequest#defaults()} for none
     * @return a paginated {@link CreatorPageResponse}
     */
    public CreatorPageResponse list(ListCreatorsRequest request) {
        if (request == null) request = ListCreatorsRequest.defaults();
        return transport.get(
            "/api/v1/integrations/creators",
            QueryParams.create()
                .add("page", request.getPage())
                .add("size", request.getSize())
                .add("status", request.getStatus() != null ? request.getStatus().getValue() : null)
                .build(),
            tokenProvider.getAuthorizationHeader(),
            CreatorPageResponse.class);
    }

    private static String encode(String value) {
        try { return java.net.URLEncoder.encode(value, "UTF-8"); }
        catch (java.io.UnsupportedEncodingException e) { return value; }
    }
}
