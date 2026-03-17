/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.qredex.sdk.exceptions.*;
import com.qredex.sdk.model.enums.ResolutionStatus;
import com.qredex.sdk.model.request.*;
import com.qredex.sdk.model.response.*;
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
    void creators_create_validationError() {
        assertThatThrownBy(() -> CreateCreatorRequest.builder().build())
            .isInstanceOf(QredexValidationException.class)
            .hasMessageContaining("handle");
    }

    @Test
    void creators_create_blankCreatorId_validation() {
        stubTokenEndpoint();
        assertThatThrownBy(() -> qredex.creators().get(""))
            .isInstanceOf(QredexValidationException.class);
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
}
