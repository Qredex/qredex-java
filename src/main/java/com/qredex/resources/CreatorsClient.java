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
import com.qredex.model.request.CreateCreatorRequest;
import com.qredex.model.request.ListCreatorsRequest;
import com.qredex.model.response.CreatorPageResponse;
import com.qredex.model.response.CreatorResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @NotNull
    public CreatorResponse create(@NotNull CreateCreatorRequest request) {
        return create(request, null);
    }

    @NotNull
    public CreatorResponse create(@NotNull CreateCreatorRequest request, @Nullable QredexCallOptions options) {
        if (request == null) {
            throw new QredexValidationException("request must not be null.");
        }
        return transport.post(
            "/api/v1/integrations/creators",
            request,
            tokenProvider.getAuthorizationHeader(),
            CreatorResponse.class,
            options);
    }

    /**
     * Retrieves a single creator by their UUID.
     *
     * @param creatorId the UUID of the creator
     * @return the {@link CreatorResponse}
     * @throws QredexValidationException if {@code creatorId} is blank
     */
    @NotNull
    public CreatorResponse get(@Nullable String creatorId) {
        return get(creatorId, null);
    }

    @NotNull
    public CreatorResponse get(@Nullable String creatorId, @Nullable QredexCallOptions options) {
        if (creatorId == null || creatorId.trim().isEmpty()) {
            throw new QredexValidationException("creatorId must not be blank.");
        }
        return transport.get(
            "/api/v1/integrations/creators/" + encode(creatorId),
            null,
            tokenProvider.getAuthorizationHeader(),
            CreatorResponse.class,
            options);
    }

    /**
     * Lists creators with optional pagination and status filter.
     *
     * @param request optional filters; use {@link ListCreatorsRequest#defaults()} for none
     * @return a paginated {@link CreatorPageResponse}
     */
    @NotNull
    public CreatorPageResponse list() {
        return list(null, null);
    }

    @NotNull
    public CreatorPageResponse list(@Nullable ListCreatorsRequest request) {
        return list(request, null);
    }

    @NotNull
    public CreatorPageResponse list(@Nullable ListCreatorsRequest request, @Nullable QredexCallOptions options) {
        if (request == null) request = ListCreatorsRequest.defaults();
        return transport.get(
            "/api/v1/integrations/creators",
            QueryParams.create()
                .add("page", request.getPage())
                .add("size", request.getSize())
                .add("status", request.getStatus() != null ? request.getStatus().getValue() : null)
                .build(),
            tokenProvider.getAuthorizationHeader(),
            CreatorPageResponse.class,
            options);
    }

    private static String encode(String value) {
        try { return java.net.URLEncoder.encode(value, "UTF-8"); }
        catch (java.io.UnsupportedEncodingException e) { return value; }
    }
}
