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
package com.qredex;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.qredex.exceptions.*;
import com.qredex.model.standards.*;
import com.qredex.model.request.*;
import com.qredex.model.response.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
class QredexIntegrationTest {

    private WireMockServer wireMock;
    private Qredex qredex;

    @BeforeAll
    void startServer() {
        wireMock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMock.start();
        WireMock.configureFor("localhost", wireMock.port());
    }

    @AfterAll
    void stopServer() {
        wireMock.stop();
    }

    @BeforeEach
    void resetAndBuildClient() {
        wireMock.resetAll();
        qredex = Qredex.builder()
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .baseUrl("http://localhost:" + wireMock.port())
            .timeoutMs(5_000)
            .build();
    }

    // -------------------------------------------------------------------------
    // Auth
    // -------------------------------------------------------------------------

    @Test
    void issueToken_happy() {
        stubTokenEndpoint();
        OAuthTokenResponse token = qredex.auth().issueToken();
        assertThat(token.getAccessToken()).isEqualTo("test-access-token");
        assertThat(token.getTokenType()).isEqualTo("Bearer");
        assertThat(token.getExpiresIn()).isEqualTo(3600L);
    }

    @Test
    void auth_tokenIsReused() {
        stubTokenEndpoint();
        // First call acquires token
        qredex.creators().list(null);
        // Second call should NOT hit the token endpoint again (cached)
        qredex.creators().list(null);
        verify(1, postRequestedFor(urlEqualTo("/api/v1/auth/token")));
    }

    @Test
    void auth_clearCache_causesNewTokenFetch() {
        stubTokenEndpoint();
        qredex.creators().list(null);
        qredex.auth().clearTokenCache();
        qredex.creators().list(null);
        verify(2, postRequestedFor(urlEqualTo("/api/v1/auth/token")));
    }

    @Test
    void auth_invalidCredentials_throwsAuthenticationException() {
        stubFor(post(urlEqualTo("/api/v1/auth/token"))
            .willReturn(aResponse()
                .withStatus(401)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error_code\":\"invalid_credentials\",\"message\":\"Invalid client credentials.\"}")));

        assertThatThrownBy(() -> qredex.auth().issueToken())
            .isInstanceOf(QredexAuthenticationException.class)
            .satisfies(e -> {
                QredexAuthenticationException ex = (QredexAuthenticationException) e;
                assertThat(ex.getStatus()).isEqualTo(401);
                assertThat(ex.getErrorCode()).isEqualTo("invalid_credentials");
            });
    }

    // -------------------------------------------------------------------------
    // Init / bootstrap patterns
    // -------------------------------------------------------------------------

    @Test
    void init_withConfig_works() {
        QredexConfig config = QredexConfig.builder()
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .baseUrl("http://localhost:" + wireMock.port())
            .build();

        Qredex client = Qredex.init(config);
        assertThat(client).isNotNull();
        assertThat(client.getConfig().getClientId()).isEqualTo("test-client-id");
    }

    @Test
    void init_nullConfig_throwsConfigurationException() {
        assertThatThrownBy(() -> Qredex.init(null))
            .isInstanceOf(QredexConfigurationException.class);
    }

    @Test
    void builder_andInit_produceEquivalentClients() {
        QredexConfig config = QredexConfig.builder()
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .baseUrl("http://localhost:" + wireMock.port())
            .build();

        Qredex viaInit    = Qredex.init(config);
        Qredex viaBuilder = Qredex.builder()
            .clientId("test-client-id")
            .clientSecret("test-client-secret")
            .baseUrl("http://localhost:" + wireMock.port())
            .build();

        assertThat(viaInit.getConfig().getClientId())
            .isEqualTo(viaBuilder.getConfig().getClientId());
        assertThat(viaInit.getConfig().getBaseUrl())
            .isEqualTo(viaBuilder.getConfig().getBaseUrl());
    }

    // -------------------------------------------------------------------------
    // Creators
    // -------------------------------------------------------------------------

    @Test
    void creators_create_happy() {
        stubTokenEndpoint();
        stubFor(post(urlEqualTo("/api/v1/integrations/creators"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/json")
                .withBody(creatorJson("c1", "alice"))));

        CreatorResponse response = qredex.creators().create(
            CreateCreatorRequest.builder().handle("alice").displayName("Alice").build());

        assertThat(response.getId()).isEqualTo("c1");
        assertThat(response.getHandle()).isEqualTo("alice");
    }

    @Test
    void creators_get_happy() {
        stubTokenEndpoint();
        stubFor(get(urlEqualTo("/api/v1/integrations/creators/c1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(creatorJson("c1", "alice"))));

        CreatorResponse response = qredex.creators().get("c1");
        assertThat(response.getId()).isEqualTo("c1");
    }

    @Test
    void creators_list_happy() {
        stubTokenEndpoint();
        stubFor(get(urlPathEqualTo("/api/v1/integrations/creators"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"items\":[" + creatorJson("c1", "alice") + "],\"page\":0,\"size\":20,\"total_elements\":1,\"total_pages\":1}")));

        CreatorPageResponse page = qredex.creators().list(null);
        assertThat(page.getItems()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(1L);
    }

    @Test
    void creators_list_withoutRequest_happy() {
        stubTokenEndpoint();
        stubFor(get(urlPathEqualTo("/api/v1/integrations/creators"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"items\":[" + creatorJson("c1", "alice") + "],\"page\":0,\"size\":20,\"total_elements\":1,\"total_pages\":1}")));

        CreatorPageResponse page = qredex.creators().list();
        assertThat(page.getItems()).hasSize(1);
    }

    @Test
    void creators_create_validationError() {
        assertThatThrownBy(() -> CreateCreatorRequest.builder().build())
            .isInstanceOf(QredexValidationException.class)
            .hasMessageContaining("handle");
    }

    @Test
    void creators_create_nullRequest_validation() {
        assertThatThrownBy(() -> qredex.creators().create(null))
            .isInstanceOf(QredexValidationException.class)
            .hasMessageContaining("request");
    }

    @Test
    void creators_create_blankCreatorId_validation() {
        stubTokenEndpoint();
        assertThatThrownBy(() -> qredex.creators().get(""))
            .isInstanceOf(QredexValidationException.class);
    }

    @Test
    void creators_requestAndResponseCollections_areImmutable() {
        java.util.Map<String, String> socials = new java.util.LinkedHashMap<String, String>();
        socials.put("instagram", "alice");

        CreateCreatorRequest request = CreateCreatorRequest.builder()
            .handle("alice")
            .socials(socials)
            .build();

        socials.put("tiktok", "alice2");
        assertThat(request.getSocials()).containsOnly(entry("instagram", "alice"));
        assertThatThrownBy(() -> request.getSocials().put("youtube", "alice3"))
            .isInstanceOf(UnsupportedOperationException.class);

        stubTokenEndpoint();
        stubFor(get(urlEqualTo("/api/v1/integrations/creators/c1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":\"c1\",\"handle\":\"alice\",\"status\":\"ACTIVE\",\"socials\":{\"instagram\":\"alice\"}}")));

        CreatorResponse response = qredex.creators().get("c1");
        assertThat(response.getSocials()).containsOnly(entry("instagram", "alice"));
        assertThatThrownBy(() -> response.getSocials().put("youtube", "alice"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    // -------------------------------------------------------------------------
    // Links
    // -------------------------------------------------------------------------

    @Test
    void links_create_happy() {
        stubTokenEndpoint();
        stubFor(post(urlEqualTo("/api/v1/integrations/links"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/json")
                .withBody(linkJson("l1", "summer-sale"))));

        LinkResponse response = qredex.links().create(
            CreateLinkRequest.builder()
                .storeId("store-uuid")
                .creatorId("creator-uuid")
                .linkName("summer-sale")
                .destinationPath("/products/summer")
                .build());

        assertThat(response.getId()).isEqualTo("l1");
        assertThat(response.getLinkName()).isEqualTo("summer-sale");
    }

    @Test
    void links_getStats_happy() {
        stubTokenEndpoint();
        stubFor(get(urlEqualTo("/api/v1/integrations/links/l1/stats"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"link_id\":\"l1\",\"clicks_count\":42,\"orders_count\":5,\"revenue_total\":550.0}")));

        LinkStatsResponse stats = qredex.links().getStats("l1");
        assertThat(stats.getClicksCount()).isEqualTo(42L);
        assertThat(stats.getOrdersCount()).isEqualTo(5L);
    }

    @Test
    void links_create_nullRequest_validation() {
        assertThatThrownBy(() -> qredex.links().create(null))
            .isInstanceOf(QredexValidationException.class)
            .hasMessageContaining("request");
    }

    @Test
    void links_list_withoutRequest_happy() {
        stubTokenEndpoint();
        stubFor(get(urlPathEqualTo("/api/v1/integrations/links"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"items\":[" + linkJson("l1", "summer-sale") + "],\"page\":0,\"size\":20,\"total_elements\":1,\"total_pages\":1}")));

        LinkPageResponse page = qredex.links().list();
        assertThat(page.getItems()).hasSize(1);
        assertThatThrownBy(() -> page.getItems().add(null))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    // -------------------------------------------------------------------------
    // Intents
    // -------------------------------------------------------------------------

    @Test
    void intents_issueInfluenceIntentToken_happy() {
        stubTokenEndpoint();
        stubFor(post(urlEqualTo("/api/v1/integrations/intents/token"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":\"iit1\",\"link_id\":\"l1\",\"token\":\"iit-token\",\"token_id\":\"tid1\"," +
                          "\"issued_at\":\"2026-03-17T00:00:00Z\",\"expires_at\":\"2026-03-17T01:00:00Z\"," +
                          "\"status\":\"ACTIVE\",\"integrity_version\":2}")));

        InfluenceIntentResponse iit = qredex.intents().issueInfluenceIntentToken(
            IssueInfluenceIntentTokenRequest.builder()
                .linkId("l1")
                .referrer("https://instagram.com")
                .build());

        assertThat(iit.getToken()).isEqualTo("iit-token");
        assertThat(iit.getLinkId()).isEqualTo("l1");
    }

    @Test
    void intents_lockPurchaseIntent_happy() {
        stubTokenEndpoint();
        stubFor(post(urlEqualTo("/api/v1/integrations/intents/lock"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":\"pit1\",\"token\":\"pit-token\",\"token_id\":\"tid2\"," +
                          "\"issued_at\":\"2026-03-17T00:00:00Z\",\"expires_at\":\"2026-03-17T01:00:00Z\"," +
                          "\"store_domain_snapshot\":\"shop.example.com\",\"integrity_version\":2," +
                          "\"eligible\":true}")));

        PurchaseIntentResponse pit = qredex.intents().lockPurchaseIntent(
            LockPurchaseIntentRequest.builder()
                .token("iit-token")
                .source("browser-cart")
                .build());

        assertThat(pit.getToken()).isEqualTo("pit-token");
        assertThat(pit.getEligible()).isTrue();
    }

    @Test
    void intents_issueIIT_missingToken_throwsValidation() {
        stubTokenEndpoint();
        assertThatThrownBy(() -> qredex.intents().issueInfluenceIntentToken(
                IssueInfluenceIntentTokenRequest.builder().linkId("").build()))
            .isInstanceOf(QredexValidationException.class);
    }

    @Test
    void intents_issueInfluenceIntentToken_nullRequest_validation() {
        assertThatThrownBy(() -> qredex.intents().issueInfluenceIntentToken(null))
            .isInstanceOf(QredexValidationException.class)
            .hasMessageContaining("request");
    }

    @Test
    void intents_lockPurchaseIntent_nullRequest_validation() {
        assertThatThrownBy(() -> qredex.intents().lockPurchaseIntent(null))
            .isInstanceOf(QredexValidationException.class)
            .hasMessageContaining("request");
    }

    // -------------------------------------------------------------------------
    // Orders
    // -------------------------------------------------------------------------

    @Test
    void orders_recordPaidOrder_happy() {
        stubTokenEndpoint();
        stubFor(post(urlEqualTo("/api/v1/integrations/orders/paid"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/json")
                .withBody(orderAttributionJson("o1", "ATTRIBUTED"))));

        OrderAttributionResponse order = qredex.orders().recordPaidOrder(
            RecordPaidOrderRequest.builder()
                .storeId("store-uuid")
                .externalOrderId("order-100")
                .currency("USD")
                .totalPrice(99.99)
                .purchaseIntentToken("pit-token")
                .build());

        assertThat(order.getId()).isEqualTo("o1");
        assertThat(order.getResolutionStatus()).isEqualTo(ResolutionStatus.ATTRIBUTED);
    }

    @Test
    void orders_list_happy() {
        stubTokenEndpoint();
        stubFor(get(urlPathEqualTo("/api/v1/integrations/orders"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"items\":[" + orderAttributionJson("o1", "ATTRIBUTED") + "]," +
                          "\"page\":0,\"size\":20,\"total_elements\":1,\"total_pages\":1}")));

        OrderAttributionPageResponse page = qredex.orders().list(null);
        assertThat(page.getItems()).hasSize(1);
        assertThat(page.getItems().get(0).getResolutionStatus()).isEqualTo(ResolutionStatus.ATTRIBUTED);
    }

    @Test
    void orders_list_withoutRequest_happy() {
        stubTokenEndpoint();
        stubFor(get(urlPathEqualTo("/api/v1/integrations/orders"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"items\":[" + orderAttributionJson("o1", "ATTRIBUTED") + "]," +
                          "\"page\":0,\"size\":20,\"total_elements\":1,\"total_pages\":1}")));

        OrderAttributionPageResponse page = qredex.orders().list();
        assertThat(page.getItems()).hasSize(1);
        assertThatThrownBy(() -> page.getItems().clear())
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void orders_getDetails_happy() {
        stubTokenEndpoint();
        stubFor(get(urlEqualTo("/api/v1/integrations/orders/o1/details"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":\"o1\",\"external_order_id\":\"order-100\",\"currency\":\"USD\"," +
                          "\"resolution_status\":\"ATTRIBUTED\",\"integrity_score\":90," +
                          "\"integrity_band\":\"HIGH\",\"review_required\":false,\"duplicate_suspect\":false," +
                          "\"created_at\":\"2026-03-17T00:00:00Z\",\"updated_at\":\"2026-03-17T00:00:00Z\"}")));

        OrderAttributionDetailsResponse details = qredex.orders().getDetails("o1");
        assertThat(details.getId()).isEqualTo("o1");
        assertThat(details.getResolutionStatus()).isEqualTo(ResolutionStatus.ATTRIBUTED);
        assertThat(details.getIntegrityScore()).isEqualTo(90);
    }

    @Test
    void orders_getDetails_nestedCollections_areImmutable() {
        stubTokenEndpoint();
        stubFor(get(urlEqualTo("/api/v1/integrations/orders/o1/details"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":\"o1\",\"resolution_status\":\"ATTRIBUTED\",\"integrity_score\":90," +
                          "\"integrity_band\":\"HIGH\",\"review_required\":false,\"duplicate_suspect\":false," +
                          "\"timeline\":[{\"event_type\":\"paid\",\"occurred_at\":\"2026-03-17T00:00:00Z\"}]," +
                          "\"score_breakdown_json\":{\"review_reasons\":[\"origin_mismatch\"]}}")));

        OrderAttributionDetailsResponse details = qredex.orders().getDetails("o1");
        assertThat(details.getTimeline()).hasSize(1);
        assertThat(details.getScoreBreakdown().getReviewReasons()).containsExactly("origin_mismatch");
        assertThatThrownBy(() -> details.getTimeline().clear())
            .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> details.getScoreBreakdown().getReviewReasons().add("duplicate"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void orders_conflict_throwsConflictException() {
        stubTokenEndpoint();
        stubFor(post(urlEqualTo("/api/v1/integrations/orders/paid"))
            .willReturn(aResponse()
                .withStatus(409)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error_code\":\"REJECTED_SOURCE_POLICY\",\"message\":\"Source policy rejected.\"}")));

        assertThatThrownBy(() -> qredex.orders().recordPaidOrder(
                RecordPaidOrderRequest.builder()
                    .storeId("s1").externalOrderId("o1").currency("USD").build()))
            .isInstanceOf(QredexConflictException.class)
            .satisfies(e -> {
                QredexConflictException ex = (QredexConflictException) e;
                assertThat(ex.getStatus()).isEqualTo(409);
                assertThat(ex.getErrorCode()).isEqualTo("REJECTED_SOURCE_POLICY");
            });
    }

    @Test
    void orders_validation_missingCurrency() {
        stubTokenEndpoint();
        assertThatThrownBy(() -> qredex.orders().recordPaidOrder(
                RecordPaidOrderRequest.builder()
                    .storeId("s1").externalOrderId("o1").currency("").build()))
            .isInstanceOf(QredexValidationException.class);
    }

    @Test
    void orders_recordPaidOrder_nullRequest_validation() {
        assertThatThrownBy(() -> qredex.orders().recordPaidOrder(null))
            .isInstanceOf(QredexValidationException.class)
            .hasMessageContaining("request");
    }

    // -------------------------------------------------------------------------
    // Refunds
    // -------------------------------------------------------------------------

    @Test
    void refunds_recordRefund_happy() {
        stubTokenEndpoint();
        stubFor(post(urlEqualTo("/api/v1/integrations/orders/refund"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(orderAttributionJson("o1", "ATTRIBUTED"))));

        OrderAttributionResponse result = qredex.refunds().recordRefund(
            RecordRefundRequest.builder()
                .storeId("store-uuid")
                .externalOrderId("order-100")
                .externalRefundId("refund-55")
                .refundTotal(99.99)
                .build());

        assertThat(result.getId()).isEqualTo("o1");
    }

    @Test
    void refunds_missingExternalRefundId_validation() {
        stubTokenEndpoint();
        assertThatThrownBy(() -> qredex.refunds().recordRefund(
                RecordRefundRequest.builder()
                    .storeId("s1").externalOrderId("o1").externalRefundId("").build()))
            .isInstanceOf(QredexValidationException.class);
    }

    @Test
    void refunds_recordRefund_nullRequest_validation() {
        assertThatThrownBy(() -> qredex.refunds().recordRefund(null))
            .isInstanceOf(QredexValidationException.class)
            .hasMessageContaining("request");
    }

    // -------------------------------------------------------------------------
    // Error parsing
    // -------------------------------------------------------------------------

    @Test
    void errorParsing_400_throwsValidationException() {
        stubTokenEndpoint();
        stubFor(post(urlEqualTo("/api/v1/integrations/creators"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error_code\":\"handle_taken\",\"message\":\"Handle already in use.\"}")));

        assertThatThrownBy(() -> qredex.creators().create(
                CreateCreatorRequest.builder().handle("taken").build()))
            .isInstanceOf(QredexValidationException.class)
            .satisfies(e -> {
                QredexValidationException ex = (QredexValidationException) e;
                assertThat(ex.getStatus()).isEqualTo(400);
                assertThat(ex.getErrorCode()).isEqualTo("handle_taken");
                assertThat(ex.getMessage()).contains("Handle already in use");
            });
    }

    @Test
    void errorParsing_403_throwsAuthorizationException() {
        stubTokenEndpoint();
        stubFor(get(urlEqualTo("/api/v1/integrations/creators/c1"))
            .willReturn(aResponse()
                .withStatus(403)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error_code\":\"insufficient_scope\",\"message\":\"Missing required scope.\"}")));

        assertThatThrownBy(() -> qredex.creators().get("c1"))
            .isInstanceOf(QredexAuthorizationException.class)
            .satisfies(e -> {
                assertThat(((QredexApiException) e).getStatus()).isEqualTo(403);
                assertThat(((QredexApiException) e).getErrorCode()).isEqualTo("insufficient_scope");
            });
    }

    @Test
    void errorParsing_404_throwsNotFoundException() {
        stubTokenEndpoint();
        stubFor(get(urlEqualTo("/api/v1/integrations/creators/missing"))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error_code\":\"not_found\",\"message\":\"Creator not found.\"}")));

        assertThatThrownBy(() -> qredex.creators().get("missing"))
            .isInstanceOf(QredexNotFoundException.class);
    }

    @Test
    void errorParsing_429_throwsRateLimitException() {
        stubTokenEndpoint();
        stubFor(post(urlEqualTo("/api/v1/integrations/orders/paid"))
            .willReturn(aResponse()
                .withStatus(429)
                .withHeader("Content-Type", "application/json")
                .withHeader("Retry-After", "60")
                .withBody("{\"error_code\":\"rate_limit_exceeded\",\"message\":\"Too many requests.\"}")));

        assertThatThrownBy(() -> qredex.orders().recordPaidOrder(
                RecordPaidOrderRequest.builder()
                    .storeId("s1").externalOrderId("o1").currency("USD").build()))
            .isInstanceOf(QredexRateLimitException.class)
            .satisfies(e -> {
                QredexRateLimitException ex = (QredexRateLimitException) e;
                assertThat(ex.getStatus()).isEqualTo(429);
                assertThat(ex.getRetryAfterSeconds()).isEqualTo(60L);
            });
    }

    @Test
    void transport_emptySuccessfulBody_throwsNetworkException() {
        stubTokenEndpoint();
        stubFor(get(urlEqualTo("/api/v1/integrations/creators/c1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("")));

        assertThatThrownBy(() -> qredex.creators().get("c1"))
            .isInstanceOf(QredexNetworkException.class)
            .hasMessageContaining("empty response body");
    }

    @Test
    void errorParsing_requestIdAndTraceId_preserved() {
        stubTokenEndpoint();
        stubFor(get(urlEqualTo("/api/v1/integrations/creators/c1"))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/json")
                .withHeader("X-Request-Id", "req-abc-123")
                .withHeader("X-Trace-Id", "trace-xyz-456")
                .withBody("{\"error_code\":\"not_found\",\"message\":\"Not found.\"}")));

        assertThatThrownBy(() -> qredex.creators().get("c1"))
            .isInstanceOf(QredexNotFoundException.class)
            .satisfies(e -> {
                QredexNotFoundException ex = (QredexNotFoundException) e;
                assertThat(ex.getRequestId()).isEqualTo("req-abc-123");
                assertThat(ex.getTraceId()).isEqualTo("trace-xyz-456");
            });
    }

    // -------------------------------------------------------------------------
    // Config validation
    // -------------------------------------------------------------------------

    @Test
    void config_missingClientId_throwsConfigurationException() {
        assertThatThrownBy(() -> Qredex.builder().clientSecret("secret").build())
            .isInstanceOf(QredexConfigurationException.class)
            .hasMessageContaining("clientId");
    }

    @Test
    void config_missingClientSecret_throwsConfigurationException() {
        assertThatThrownBy(() -> Qredex.builder().clientId("id").build())
            .isInstanceOf(QredexConfigurationException.class)
            .hasMessageContaining("clientSecret");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void stubTokenEndpoint() {
        stubFor(post(urlEqualTo("/api/v1/auth/token"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"access_token\":\"test-access-token\",\"token_type\":\"Bearer\"," +
                          "\"expires_in\":3600,\"scope\":\"direct:api\"}")));

        // Also stub the creators list for token-reuse test
        stubFor(get(urlPathEqualTo("/api/v1/integrations/creators"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"items\":[],\"page\":0,\"size\":20,\"total_elements\":0,\"total_pages\":0}")));
    }

    private static String creatorJson(String id, String handle) {
        return "{\"id\":\"" + id + "\",\"handle\":\"" + handle + "\",\"status\":\"ACTIVE\"," +
               "\"created_at\":\"2026-03-17T00:00:00Z\",\"updated_at\":\"2026-03-17T00:00:00Z\"}";
    }

    private static String linkJson(String id, String name) {
        return "{\"id\":\"" + id + "\",\"link_name\":\"" + name + "\",\"link_code\":\"abc123\"," +
               "\"status\":\"ACTIVE\",\"destination_path\":\"/products/summer\"," +
               "\"attribution_window_days\":30,\"created_at\":\"2026-03-17T00:00:00Z\"," +
               "\"updated_at\":\"2026-03-17T00:00:00Z\"}";
    }

    private static String orderAttributionJson(String id, String resolutionStatus) {
        return "{\"id\":\"" + id + "\",\"external_order_id\":\"order-100\",\"currency\":\"USD\"," +
               "\"resolution_status\":\"" + resolutionStatus + "\",\"integrity_score\":85," +
               "\"integrity_band\":\"HIGH\",\"review_required\":false,\"duplicate_suspect\":false," +
               "\"order_source\":\"DIRECT_API\",\"created_at\":\"2026-03-17T00:00:00Z\"," +
               "\"updated_at\":\"2026-03-17T00:00:00Z\"}";
    }

    // -------------------------------------------------------------------------
    // QredexScope enum
    // -------------------------------------------------------------------------

    @Test
    void scope_join_single() {
        assertThat(QredexScope.join(QredexScope.API)).isEqualTo("direct:api");
    }

    @Test
    void scope_join_multiple() {
        String joined = QredexScope.join(
            QredexScope.LINKS_WRITE, QredexScope.INTENTS_WRITE, QredexScope.ORDERS_WRITE);
        assertThat(joined).isEqualTo("direct:links:write direct:intents:write direct:orders:write");
    }

    @Test
    void scope_join_empty_returnsNull() {
        assertThat(QredexScope.join()).isNull();
    }

    @Test
    void scope_fromValue() {
        assertThat(QredexScope.fromValue("direct:api")).isEqualTo(QredexScope.API);
        assertThat(QredexScope.fromValue("direct:orders:write")).isEqualTo(QredexScope.ORDERS_WRITE);
    }

    @Test
    void scope_enum_inBuilder() {
        Qredex client = Qredex.builder()
            .clientId("test-id")
            .clientSecret("test-secret")
            .baseUrl("http://localhost:" + wireMock.port())
            .scopes(QredexScope.ORDERS_WRITE, QredexScope.INTENTS_WRITE)
            .build();
        assertThat(client.getConfig().getScope())
            .isEqualTo("direct:orders:write direct:intents:write");
    }

    // -------------------------------------------------------------------------
    // Forward-compatible enum deserialization
    // -------------------------------------------------------------------------

    @Test
    void enum_unknownResolutionStatus_deserializesToUnknown() {
        stubTokenEndpoint();
        stubFor(post(urlEqualTo("/api/v1/integrations/orders/paid"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":\"o1\",\"external_order_id\":\"order-100\",\"currency\":\"USD\"," +
                          "\"resolution_status\":\"FUTURE_VALUE\",\"integrity_score\":50," +
                          "\"integrity_band\":\"FUTURE_BAND\",\"review_required\":false," +
                          "\"duplicate_suspect\":false,\"order_source\":\"FUTURE_SOURCE\"," +
                          "\"created_at\":\"2026-03-17T00:00:00Z\",\"updated_at\":\"2026-03-17T00:00:00Z\"}")));

        OrderAttributionResponse order = qredex.orders().recordPaidOrder(
            RecordPaidOrderRequest.builder()
                .storeId("store-uuid")
                .externalOrderId("order-100")
                .currency("USD")
                .build());

        assertThat(order.getResolutionStatus()).isEqualTo(ResolutionStatus.UNKNOWN);
        assertThat(order.getIntegrityBand()).isEqualTo(IntegrityBand.UNKNOWN);
        assertThat(order.getOrderSource()).isEqualTo(OrderSource.UNKNOWN);
    }

    @Test
    void enum_unknownCreatorStatus_deserializesToUnknown() {
        stubTokenEndpoint();
        stubFor(get(urlEqualTo("/api/v1/integrations/creators/c1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":\"c1\",\"handle\":\"alice\",\"status\":\"SUSPENDED\"," +
                          "\"created_at\":\"2026-03-17T00:00:00Z\",\"updated_at\":\"2026-03-17T00:00:00Z\"}")));

        CreatorResponse response = qredex.creators().get("c1");
        assertThat(response.getStatus()).isEqualTo(CreatorStatus.UNKNOWN);
    }

    // -------------------------------------------------------------------------
    // Token expiry → automatic refresh
    // -------------------------------------------------------------------------

    @Test
    void auth_expiredToken_triggersRefresh() {
        // Issue a token with 1-second expiry (below 30s refresh window → expires immediately)
        stubFor(post(urlEqualTo("/api/v1/auth/token"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"access_token\":\"token-1\",\"token_type\":\"Bearer\"," +
                          "\"expires_in\":1,\"scope\":\"direct:api\"}")));

        stubFor(get(urlPathEqualTo("/api/v1/integrations/creators"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"items\":[],\"page\":0,\"size\":20,\"total_elements\":0,\"total_pages\":0}")));

        // First call — acquires token
        qredex.creators().list(null);
        // Second call — token with 1s expiry is already past the 30s refresh window, so re-fetches
        qredex.creators().list(null);

        // Token endpoint should have been called twice (expired → refreshed)
        verify(2, postRequestedFor(urlEqualTo("/api/v1/auth/token")));
    }

    // -------------------------------------------------------------------------
    // Canonical end-to-end flow: IIT → PIT → order → refund
    // -------------------------------------------------------------------------

    @Test
    void canonicalFlow_iitToPitToOrderToRefund() {
        stubTokenEndpoint();

        // Stub IIT issuance
        stubFor(post(urlEqualTo("/api/v1/integrations/intents/token"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":\"iit1\",\"link_id\":\"l1\",\"token\":\"iit-token-abc\"," +
                          "\"token_id\":\"tid1\",\"issued_at\":\"2026-03-17T00:00:00Z\"," +
                          "\"expires_at\":\"2026-03-17T01:00:00Z\",\"status\":\"ACTIVE\"," +
                          "\"integrity_version\":2}")));

        // Stub PIT lock
        stubFor(post(urlEqualTo("/api/v1/integrations/intents/lock"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\":\"pit1\",\"token\":\"pit-token-xyz\",\"token_id\":\"tid2\"," +
                          "\"issued_at\":\"2026-03-17T00:00:00Z\"," +
                          "\"expires_at\":\"2026-03-17T01:00:00Z\"," +
                          "\"store_domain_snapshot\":\"shop.example.com\"," +
                          "\"integrity_version\":2,\"eligible\":true," +
                          "\"window_status\":\"WITHIN\",\"origin_match_status\":\"MATCH\"}")));

        // Stub paid order
        stubFor(post(urlEqualTo("/api/v1/integrations/orders/paid"))
            .willReturn(aResponse()
                .withStatus(201)
                .withHeader("Content-Type", "application/json")
                .withBody(orderAttributionJson("oa1", "ATTRIBUTED"))));

        // Stub refund
        stubFor(post(urlEqualTo("/api/v1/integrations/orders/refund"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(orderAttributionJson("oa1", "ATTRIBUTED"))));

        // --- Execute canonical flow ---

        // Step 1: Issue IIT
        InfluenceIntentResponse iit = qredex.intents().issueInfluenceIntentToken(
            IssueInfluenceIntentTokenRequest.builder()
                .linkId("l1")
                .landingPath("/products/spring")
                .build());
        assertThat(iit.getToken()).isEqualTo("iit-token-abc");

        // Step 2: Lock PIT using IIT token
        PurchaseIntentResponse pit = qredex.intents().lockPurchaseIntent(
            LockPurchaseIntentRequest.builder()
                .token(iit.getToken())
                .source("backend-cart")
                .build());
        assertThat(pit.getToken()).isEqualTo("pit-token-xyz");
        assertThat(pit.getEligible()).isTrue();
        assertThat(pit.getWindowStatus()).isEqualTo(WindowStatus.WITHIN);

        // Step 3: Record paid order with PIT
        OrderAttributionResponse order = qredex.orders().recordPaidOrder(
            RecordPaidOrderRequest.builder()
                .storeId("store-uuid")
                .externalOrderId("order-e2e-1")
                .currency("USD")
                .totalPrice(99.99)
                .purchaseIntentToken(pit.getToken())
                .build());
        assertThat(order.getResolutionStatus()).isEqualTo(ResolutionStatus.ATTRIBUTED);

        // Step 4: Record refund
        OrderAttributionResponse refund = qredex.refunds().recordRefund(
            RecordRefundRequest.builder()
                .storeId("store-uuid")
                .externalOrderId("order-e2e-1")
                .externalRefundId("refund-e2e-1")
                .refundTotal(99.99)
                .build());
        assertThat(refund.getId()).isEqualTo("oa1");
    }

    // -------------------------------------------------------------------------
    // Closeable
    // -------------------------------------------------------------------------

    @Test
    void close_doesNotThrow() {
        Qredex client = Qredex.builder()
            .clientId("test-id")
            .clientSecret("test-secret")
            .baseUrl("http://localhost:" + wireMock.port())
            .build();
        assertThatCode(client::close).doesNotThrowAnyException();
    }

    // -------------------------------------------------------------------------
    // Builder validation — all builders now throw QredexValidationException
    // -------------------------------------------------------------------------

    @Test
    void builders_throwQredexValidationException() {
        assertThatThrownBy(() -> CreateCreatorRequest.builder().build())
            .isInstanceOf(QredexValidationException.class);
        assertThatThrownBy(() -> CreateLinkRequest.builder()
                .storeId("s").creatorId("c").linkName("n").build())
            .isInstanceOf(QredexValidationException.class);
        assertThatThrownBy(() -> IssueInfluenceIntentTokenRequest.builder().build())
            .isInstanceOf(QredexValidationException.class);
        assertThatThrownBy(() -> LockPurchaseIntentRequest.builder().build())
            .isInstanceOf(QredexValidationException.class);
        assertThatThrownBy(() -> RecordPaidOrderRequest.builder()
                .storeId("s").externalOrderId("o").build())
            .isInstanceOf(QredexValidationException.class);
        assertThatThrownBy(() -> RecordRefundRequest.builder()
                .storeId("s").externalOrderId("o").build())
            .isInstanceOf(QredexValidationException.class);
    }

    // -------------------------------------------------------------------------
    // toString coverage
    // -------------------------------------------------------------------------

    @Test
    void toString_requestAndResponse_notDefault() {
        CreateCreatorRequest req = CreateCreatorRequest.builder()
            .handle("alice").displayName("Alice").build();
        assertThat(req.toString()).contains("alice");

        // Verify response toString after deserialization
        stubTokenEndpoint();
        stubFor(get(urlEqualTo("/api/v1/integrations/creators/c1"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(creatorJson("c1", "alice"))));

        CreatorResponse resp = qredex.creators().get("c1");
        assertThat(resp.toString()).contains("c1").contains("alice");
    }
}
