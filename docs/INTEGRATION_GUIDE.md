<!--
   Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
   Licensed under the Apache License, Version 2.0.
-->

# Qredex Java SDK — Integration Guide

This guide walks through a complete Qredex machine integration using the Java SDK.

---

## Prerequisites

- A Qredex merchant account with Integration API credentials (client ID + secret)
- Java 8+ and Maven 3.6+
- The `com.qredex:qredex-java` dependency in your project

---

## Step 1 — Configure the client

### Recommended: environment-based bootstrap

Set environment variables and call `Qredex.bootstrap()`:

```bash
export QREDEX_CLIENT_ID=your-client-id
export QREDEX_CLIENT_SECRET=your-client-secret
export QREDEX_ENVIRONMENT=production
```

```java
Qredex qredex = Qredex.bootstrap();
```

### Explicit configuration

```java
Qredex qredex = Qredex.builder()
    .clientId(System.getenv("QREDEX_CLIENT_ID"))
    .clientSecret(System.getenv("QREDEX_CLIENT_SECRET"))
    .environment(QredexEnvironment.PRODUCTION)
    .timeoutMs(10_000)
    .logger(new QredexLogger() {
        public void debug(String m) { log.debug(m); }
        public void info(String m)  { log.info(m); }
        public void warn(String m)  { log.warn(m); }
        public void error(String m) { log.error(m); }
    })
    .build();
```

**Important:** Never log or store the `clientSecret`. The SDK never passes it to the logger.

---

## Step 2 — Create creators

A creator represents an influencer or affiliate whose links you track.

```java
CreatorResponse creator = qredex.creators().create(
    CreateCreatorRequest.builder()
        .handle("alice-2026")         // required — unique slug
        .displayName("Alice")         // optional
        .email("alice@example.com")   // optional
        .build());

System.out.println(creator.getId());     // UUID
System.out.println(creator.getHandle()); // "alice-2026"
```

Retrieve an existing creator:

```java
CreatorResponse creator = qredex.creators().get(creatorId);
```

List creators:

```java
CreatorPageResponse page = qredex.creators().list(
    ListCreatorsRequest.builder()
        .status(CreatorStatus.ACTIVE)
        .page(0).size(50)
        .build());
```

---

## Step 3 — Create influence links

Each link ties a creator to a destination in your store.

```java
LinkResponse link = qredex.links().create(
    CreateLinkRequest.builder()
        .storeId("your-store-uuid")
        .creatorId(creator.getId())
        .linkName("alice-spring-2026")
        .destinationPath("/collections/spring-2026")
        .attributionWindowDays(30)        // how long after click the PIT is valid
        .discountCode("ALICE20")          // optional discount attached to link
        .build());

// Share link.getPublicLinkUrl() with the creator
System.out.println(link.getPublicLinkUrl());
```

---

## Step 4 — Issue an IIT (machine flow)

Use this SDK when your backend processes click events or explicitly controls the canonical machine flow:

```java
InfluenceIntentResponse iit = qredex.intents().issueInfluenceIntentToken(
    IssueInfluenceIntentTokenRequest.builder()
        .linkId(link.getId())
        .referrer("https://instagram.com/alice")
        .landingPath("/collections/spring-2026")
        .ipHash(hashedIp)          // optional — for duplicate detection
        .userAgentHash(hashedUa)   // optional
        .build());

String iitToken = iit.getToken();  // preserve this for step 5
```

---

## Step 5 — Lock the Purchase Intent Token (PIT)

At add-to-cart time, lock the IIT into a PIT. This snapshots attribution state for the order.

### Machine-to-machine path

```java
PurchaseIntentResponse pit = qredex.intents().lockPurchaseIntent(
    LockPurchaseIntentRequest.builder()
        .token(iitToken)            // the IIT token string from step 4
        .source("backend-cart")
        .build());

String pitToken = pit.getToken();  // preserve this — submit with the order
```

Key response fields to inspect:

| Field | Meaning |
|-------|---------|
| `getToken()` | PIT token — pass to `recordPaidOrder` |
| `getEligible()` | Pre-check eligibility hint |
| `getWindowStatus()` | `WITHIN` / `OUTSIDE` attribution window |
| `getOriginMatchStatus()` | `MATCH` / `MISMATCH` / `ABSENT` |

---

## Step 6 — Record a paid order

Submit the order to Qredex when payment is confirmed. Include the PIT token.

```java
OrderAttributionResponse order = qredex.orders().recordPaidOrder(
    RecordPaidOrderRequest.builder()
        .storeId("your-store-uuid")
        .externalOrderId("order-100045")    // your stable order ID
        .orderNumber("100045")              // optional human-readable number
        .paidAt("2026-03-17T12:45:00Z")     // ISO-8601 UTC
        .currency("USD")
        .subtotalPrice(118.00)
        .discountTotal(8.00)
        .totalPrice(110.00)
        .customerEmailHash(sha256Hash(customerEmail))  // optional, for dedup
        .checkoutToken(checkoutToken)                  // optional
        .purchaseIntentToken(pitToken)                 // ← attribution key
        .build());
```

### Ingestion decisions

The API always returns HTTP 200 for successful ingestion (first write or idempotent replay).
It returns HTTP 409 for policy rejections.

| `resolutionStatus` | Meaning |
|--------------------|---------|
| `ATTRIBUTED` | Order attributed to a creator |
| `UNATTRIBUTED` | No valid PIT present |
| `REJECTED` | Policy or integrity rejection |

**Idempotency:** Safe to retry on timeout. The same `(store_id, external_order_id)` is idempotent.

Conflict rejections (HTTP 409) raise `QredexConflictException`. Check `e.getErrorCode()`:
- `REJECTED_SOURCE_POLICY` — source policy disallows this order source
- `REJECTED_CROSS_SOURCE_DUPLICATE` — order already exists under a different source

---

## Step 7 — List and inspect orders

```java
// List orders
OrderAttributionPageResponse page = qredex.orders().list(
    ListOrdersRequest.builder().page(0).size(20).build());

// Get full details including score breakdown and timeline
OrderAttributionDetailsResponse details = qredex.orders().getDetails(orderId);

System.out.println("tokenIntegrity=" + details.getTokenIntegrity());
System.out.println("integrityReason=" + details.getIntegrityReason());
System.out.println("integrityScore=" + details.getIntegrityScore());
System.out.println("integrityBand=" + details.getIntegrityBand());
```

---

## Step 8 — Record a refund

```java
OrderAttributionResponse refunded = qredex.refunds().recordRefund(
    RecordRefundRequest.builder()
        .storeId("your-store-uuid")
        .externalOrderId("order-100045")   // matches the original order
        .externalRefundId("refund-987")    // your stable refund ID
        .refundTotal(110.00)
        .refundedAt("2026-03-18T09:00:00Z")
        .build());
```

**Idempotency:** Safe to retry. Same `(store_id, external_refund_id)` is idempotent.

---

## Observability

### Logging

Pass a `QredexLogger` to see sanitized request/response logs:

```java
Qredex.builder()
    .clientId(clientId)
    .clientSecret(clientSecret)
    .logger(new QredexLogger() {
        public void debug(String m) { log.debug(m); }
        public void info(String m)  { log.info(m); }
        public void warn(String m)  { log.warn(m); }
        public void error(String m) { log.error(m); }
    });
```

Logged information: HTTP method, path, status, request ID. **Secrets and tokens are never logged.**

---

## Canonical naming reference

| Term | Meaning |
|------|---------|
| IIT | Influence Intent Token — issued on click |
| PIT | Purchase Intent Token — locked at cart |
| Order Attribution | A recorded and attributed paid order |
| `token_integrity` | `VALID` or `INVALID` |
| `integrity_reason` | Why PIT failed: `MISSING`, `TAMPERED`, `EXPIRED`, etc. |
| `resolution_status` | `ATTRIBUTED`, `UNATTRIBUTED`, or `REJECTED` |
| `integrity_score` | 0–100 confidence score |
| `integrity_band` | `HIGH`, `MEDIUM`, `LOW`, `CRITICAL` |
