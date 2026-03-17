/**
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
package com.qredex.sdk;

import com.qredex.sdk.model.standards.*;
import com.qredex.sdk.model.request.*;
import com.qredex.sdk.model.response.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Live integration tests against real Qredex API.
 *
 * <p>These tests are SKIPPED by default unless:
 * <ul>
 *   <li>QREDEX_LIVE_ENABLED=1</li>
 *   <li>QREDEX_LIVE_CLIENT_ID is set</li>
 *   <li>QREDEX_LIVE_CLIENT_SECRET is set</li>
 *   <li>QREDEX_LIVE_STORE_ID is set</li>
 * </ul>
 *
 * <p>Run with: {@code mvn test -Dgroups=live}
 *
 * <p>Example .env:
 * <pre>
 * QREDEX_LIVE_ENABLED=1
 * QREDEX_LIVE_ENVIRONMENT=staging
 * QREDEX_LIVE_CLIENT_ID=your-client-id
 * QREDEX_LIVE_CLIENT_SECRET=your-client-secret
 * QREDEX_LIVE_STORE_ID=your-store-uuid
 * </pre>
 */
@Tag("live")
class QredexLiveTest {

    private static Qredex qredex;

    @BeforeAll
    static void setUp() {
        // Check if live tests are enabled
        String liveEnabled = System.getenv("QREDEX_LIVE_ENABLED");
        assumeTrue("1".equals(liveEnabled), "Live tests disabled (QREDEX_LIVE_ENABLED != 1)");

        // Check required environment variables
        String clientId = System.getenv("QREDEX_LIVE_CLIENT_ID");
        String clientSecret = System.getenv("QREDEX_LIVE_CLIENT_SECRET");
        String storeId = System.getenv("QREDEX_LIVE_STORE_ID");

        assumeTrue(clientId != null && !clientId.isEmpty(), "QREDEX_LIVE_CLIENT_ID not set");
        assumeTrue(clientSecret != null && !clientSecret.isEmpty(), "QREDEX_LIVE_CLIENT_SECRET not set");
        assumeTrue(storeId != null && !storeId.isEmpty(), "QREDEX_LIVE_STORE_ID not set");

        // Determine environment
        String envStr = System.getenv("QREDEX_LIVE_ENVIRONMENT");
        QredexEnvironment environment = envStr != null
            ? QredexEnvironment.fromString(envStr)
            : QredexEnvironment.STAGING;

        // Initialize SDK
        qredex = Qredex.builder()
            .clientId(clientId)
            .clientSecret(clientSecret)
            .environment(environment)
            .build();

        System.out.println("Live tests running against: " + environment);
    }

    @AfterAll
    static void tearDown() {
        if (qredex != null) {
            qredex.close();
        }
    }

    @Test
    @DisplayName("Canonical flow: creator → link → IIT → PIT → order → refund")
    @Timeout(60)
    void canonicalFlow() {
        String suffix = String.valueOf(System.currentTimeMillis());
        String storeId = System.getenv("QREDEX_LIVE_STORE_ID");

        // 1. Create creator
        CreatorResponse creator = qredex.creators().create(
            CreateCreatorRequest.builder()
                .handle("codex-" + suffix)
                .displayName("Codex " + suffix)
                .build());
        assertThat(creator.getId()).isNotNull();
        assertThat(creator.getHandle()).isEqualTo("codex-" + suffix);

        // 2. Get creator
        CreatorResponse fetchedCreator = qredex.creators().get(creator.getId());
        assertThat(fetchedCreator.getId()).isEqualTo(creator.getId());

        // 3. List creators
        CreatorPageResponse listedCreators = qredex.creators().list(
            ListCreatorsRequest.builder().status(CreatorStatus.ACTIVE).build());
        assertThat(listedCreators.getItems()).isNotEmpty();

        // 4. Create link
        LinkResponse link = qredex.links().create(
            CreateLinkRequest.builder()
                .storeId(storeId)
                .creatorId(creator.getId())
                .linkName("codex-link-" + suffix)
                .destinationPath("/products/codex-" + suffix)
                .attributionWindowDays(30)
                .build());
        assertThat(link.getId()).isNotNull();
        assertThat(link.getLinkName()).isEqualTo("codex-link-" + suffix);

        // 5. Get link
        LinkResponse fetchedLink = qredex.links().get(link.getId());
        assertThat(fetchedLink.getId()).isEqualTo(link.getId());

        // 6. List links
        LinkPageResponse listedLinks = qredex.links().list(
            ListLinksRequest.builder().status(LinkStatus.ACTIVE).build());
        assertThat(listedLinks.getItems()).isNotEmpty();

        // 7. Issue IIT
        InfluenceIntentResponse iit = qredex.intents().issueInfluenceIntentToken(
            IssueInfluenceIntentTokenRequest.builder()
                .linkId(link.getId())
                .landingPath("/products/codex-" + suffix)
                .build());
        assertThat(iit.getToken()).isNotEmpty();
        assertThat(iit.getLinkId()).isEqualTo(link.getId());

        // 8. Lock PIT
        PurchaseIntentResponse pit = qredex.intents().lockPurchaseIntent(
            LockPurchaseIntentRequest.builder()
                .token(iit.getToken())
                .source("live-integration-test")
                .build());
        assertThat(pit.getToken()).isNotEmpty();

        // 9. Record paid order
        OrderAttributionResponse order = qredex.orders().recordPaidOrder(
            RecordPaidOrderRequest.builder()
                .storeId(storeId)
                .externalOrderId("codex-order-" + suffix)
                .currency("USD")
                .totalPrice(100.00)
                .purchaseIntentToken(pit.getToken())
                .build());
        assertThat(order.getId()).isNotNull();
        assertThat(order.getExternalOrderId()).isEqualTo("codex-order-" + suffix);

        // 10. List orders
        OrderAttributionPageResponse listedOrders = qredex.orders().list(
            ListOrdersRequest.builder().page(0).size(20).build());
        assertThat(listedOrders.getItems()).isNotEmpty();

        // 11. Get order details
        OrderAttributionDetailsResponse orderDetails = qredex.orders().getDetails(order.getId());
        assertThat(orderDetails.getId()).isEqualTo(order.getId());
        assertThat(orderDetails.getExternalOrderId()).isEqualTo("codex-order-" + suffix);

        // 12. Record refund
        OrderAttributionResponse refund = qredex.refunds().recordRefund(
            RecordRefundRequest.builder()
                .storeId(storeId)
                .externalOrderId("codex-order-" + suffix)
                .externalRefundId("codex-refund-" + suffix)
                .refundTotal(100.00)
                .build());
        assertThat(refund.getId()).isEqualTo(order.getId());
    }
}
