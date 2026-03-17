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
package examples;

import com.qredex.sdk.Qredex;
import com.qredex.sdk.model.standards.ResolutionStatus;
import com.qredex.sdk.model.request.*;
import com.qredex.sdk.model.response.*;

/**
 * End-to-end Qredex SDK usage examples.
 *
 * Demonstrates the canonical IIT → PIT → order → refund flow.
 */
public final class CanonicalFlow {

    public static void main(String[] args) {
        // ─────────────────────────────────────────────────────────────────────
        // 1. Initialize the client
        // ─────────────────────────────────────────────────────────────────────

        // Option A: Bootstrap from environment variables
        //   QREDEX_CLIENT_ID, QREDEX_CLIENT_SECRET, QREDEX_ENVIRONMENT (optional)
        // Qredex qredex = Qredex.bootstrap();

        // Option B: Explicit configuration
        Qredex qredex = Qredex.builder()
            .clientId(System.getenv("QREDEX_CLIENT_ID"))
            .clientSecret(System.getenv("QREDEX_CLIENT_SECRET"))
            // .environment(QredexEnvironment.STAGING) // for sandbox testing
            .build();

        System.out.println("Qredex SDK initialized.");

        // ─────────────────────────────────────────────────────────────────────
        // 2. Create a creator
        // ─────────────────────────────────────────────────────────────────────

        CreatorResponse creator = qredex.creators().create(
            CreateCreatorRequest.builder()
                .handle("alice-spring-2026")
                .displayName("Alice")
                .email("alice@example.com")
                .build());

        System.out.println("Created creator: " + creator.getId() + " (" + creator.getHandle() + ")");

        // ─────────────────────────────────────────────────────────────────────
        // 3. Create a link
        // ─────────────────────────────────────────────────────────────────────

        String storeId = "61abc354-dd8d-4a23-be02-ece77b1b4da6"; // your store UUID

        LinkResponse link = qredex.links().create(
            CreateLinkRequest.builder()
                .storeId(storeId)
                .creatorId(creator.getId())
                .linkName("alice-spring-launch")
                .destinationPath("/collections/spring-2026")
                .attributionWindowDays(30)
                .discountCode("SPRING30")
                .build());

        System.out.println("Created link: " + link.getId() + " -> " + link.getPublicLinkUrl());

        // ─────────────────────────────────────────────────────────────────────
        // 4. Issue an Influence Intent Token (IIT) — backend click flow
        // ─────────────────────────────────────────────────────────────────────

        InfluenceIntentResponse iit = qredex.intents().issueInfluenceIntentToken(
            IssueInfluenceIntentTokenRequest.builder()
                .linkId(link.getId())
                .referrer("https://instagram.com/alice")
                .landingPath("/collections/spring-2026")
                .build());

        System.out.println("Issued IIT token: " + iit.getTokenId() + " (expires " + iit.getExpiresAt() + ")");

        // ─────────────────────────────────────────────────────────────────────
        // 5. Lock a Purchase Intent Token (PIT) — at cart/add-to-cart time
        // ─────────────────────────────────────────────────────────────────────

        PurchaseIntentResponse pit = qredex.intents().lockPurchaseIntent(
            LockPurchaseIntentRequest.builder()
                .token(iit.getToken())   // the IIT token string from step 4
                .source("backend-cart")
                .build());

        System.out.println("Locked PIT: " + pit.getTokenId() +
            " eligible=" + pit.getEligible() +
            " windowStatus=" + pit.getWindowStatus());

        // ─────────────────────────────────────────────────────────────────────
        // 6. Record a paid order
        // ─────────────────────────────────────────────────────────────────────

        OrderAttributionResponse order = qredex.orders().recordPaidOrder(
            RecordPaidOrderRequest.builder()
                .storeId(storeId)
                .externalOrderId("order-100045")
                .orderNumber("100045")
                .paidAt("2026-03-17T12:45:00Z")
                .currency("USD")
                .subtotalPrice(118.00)
                .discountTotal(8.00)
                .totalPrice(110.00)
                .customerEmailHash("9f86d081884c7d659a2feaa0c55ad015")
                .purchaseIntentToken(pit.getToken())  // PIT from step 5
                .build());

        System.out.println("Recorded order: " + order.getId() +
            " resolutionStatus=" + order.getResolutionStatus() +
            " integrityScore=" + order.getIntegrityScore());

        if (order.getResolutionStatus() == ResolutionStatus.ATTRIBUTED) {
            System.out.println("  ✓ Order attributed to creator: " + order.getCreatorHandle());
        }

        // ─────────────────────────────────────────────────────────────────────
        // 7. List orders
        // ─────────────────────────────────────────────────────────────────────

        OrderAttributionPageResponse orders = qredex.orders().list(
            ListOrdersRequest.builder().page(0).size(20).build());

        System.out.println("Listed " + orders.getTotalElements() + " order(s).");
        for (OrderAttributionResponse o : orders.getItems()) {
            System.out.println("  - " + o.getExternalOrderId() + " " + o.getResolutionStatus());
        }

        // ─────────────────────────────────────────────────────────────────────
        // 8. Get order details (with score breakdown and timeline)
        // ─────────────────────────────────────────────────────────────────────

        OrderAttributionDetailsResponse details = qredex.orders().getDetails(order.getId());
        System.out.println("Order details: integrityBand=" + details.getIntegrityBand() +
            " tokenIntegrity=" + details.getTokenIntegrity() +
            " reviewRequired=" + details.isReviewRequired());

        // ─────────────────────────────────────────────────────────────────────
        // 9. Record a refund
        // ─────────────────────────────────────────────────────────────────────

        OrderAttributionResponse refunded = qredex.refunds().recordRefund(
            RecordRefundRequest.builder()
                .storeId(storeId)
                .externalOrderId("order-100045")
                .externalRefundId("refund-98765")
                .refundTotal(110.00)
                .refundedAt("2026-03-18T09:00:00Z")
                .build());

        System.out.println("Recorded refund: " + refunded.getId());
    }
}
