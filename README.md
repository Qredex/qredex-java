<!--
     ▄▄▄▄
   ▄█▀▀███▄▄              █▄
   ██    ██ ▄             ██
   ██    ██ ████▄▄█▀█▄ ▄████ ▄█▀█▄▀██ ██▀
   ██  ▄ ██ ██   ██▄█▀ ██ ██ ██▄█▀  ███
    ▀█████▄▄█▀  ▄▀█▄▄▄▄█▀███▄▀█▄▄▄▄██ ██▄
         ▀█

   Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.

   DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.

   Licensed under the Apache License, Version 2.0. See LICENSE for the full license text.
   You may not use this file except in compliance with that License.
   Unless required by applicable law or agreed to in writing, software distributed under the
   License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
   either express or implied. See the License for the specific language governing permissions
   and limitations under the License.

   If you need additional information or have any questions, please email: copyright@qredex.com
-->

# `qredex-java`

[![CI](https://github.com/Qredex/qredex-java/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Qredex/qredex-java/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/java-8%2B-orange.svg)](https://www.java.com/)

Canonical Java server SDK for Qredex machine-to-machine integrations.

`qredex` for Java is built for backend systems that need to create creators and links, issue IITs, lock PITs, and record paid orders and refunds without dealing with raw HTTP plumbing.

## Requirements

- Java 8+
- Maven 3.6+ or Gradle 7+

## Installation

**Maven:**

```xml
<dependency>
    <groupId>com.qredex</groupId>
    <artifactId>qredex-java</artifactId>
    <version>0.1.0</version>
</dependency>
```

**Gradle:**

```groovy
implementation 'com.qredex:qredex-java:0.1.0'
```

## Quick Start

Set:

- `QREDEX_CLIENT_ID`
- `QREDEX_CLIENT_SECRET`

Then run the canonical server-side flow:

```java
Qredex qredex = Qredex.bootstrap();

String storeId = System.getenv("QREDEX_STORE_ID");

CreatorResponse creator = qredex.creators().create(
    CreateCreatorRequest.builder()
        .handle("alice")
        .displayName("Alice")
        .build());

LinkResponse link = qredex.links().create(
    CreateLinkRequest.builder()
        .storeId(storeId)
        .creatorId(creator.getId())
        .linkName("spring-launch")
        .destinationPath("/products/spring-launch")
        .build());

InfluenceIntentResponse iit = qredex.intents().issueInfluenceIntentToken(
    IssueInfluenceIntentTokenRequest.builder()
        .linkId(link.getId())
        .landingPath("/products/spring-launch")
        .build());

PurchaseIntentResponse pit = qredex.intents().lockPurchaseIntent(
    LockPurchaseIntentRequest.builder()
        .token(iit.getToken())
        .source("backend-cart")
        .build());

OrderAttributionResponse order = qredex.orders().recordPaidOrder(
    RecordPaidOrderRequest.builder()
        .storeId(storeId)
        .externalOrderId("order-100045")
        .currency("USD")
        .totalPrice(110.00)
        .purchaseIntentToken(pit.getToken())
        .build());

qredex.refunds().recordRefund(
    RecordRefundRequest.builder()
        .storeId(storeId)
        .externalOrderId("order-100045")
        .externalRefundId("refund-100045-1")
        .refundTotal(25.00)
        .build());
```

## Why This SDK

- automatic client-credentials auth with token caching
- typed request objects instead of raw maps or long parameter lists
- typed responses that preserve canonical Qredex field names (`token_integrity`, `integrity_reason`, `resolution_status`)
- structured exception hierarchy with `status`, `errorCode`, `requestId`, and `traceId`
- deterministic behavior aligned with the canonical machine integration flow: IIT → PIT → order → refund
- zero framework dependencies — works in any Java 8+ backend

## Initialization

Three equivalent paths, choose the one that fits your service:

```java
// 1. Bootstrap from environment variables — preferred for environment-configured services
Qredex qredex = Qredex.bootstrap();

// 2. Explicit config object — preferred when you manage config programmatically
Qredex qredex = Qredex.init(
    QredexConfig.builder()
        .clientId("your-client-id")
        .clientSecret("your-client-secret")
        .environment(QredexEnvironment.PRODUCTION)
        .build()
);

// 3. Fluent builder — convenience alias for Qredex.init()
Qredex qredex = Qredex.builder()
    .clientId("your-client-id")
    .clientSecret("your-client-secret")
    .build();
```

`Qredex.bootstrap()` reads `QREDEX_CLIENT_ID`, `QREDEX_CLIENT_SECRET`, optional `QREDEX_SCOPE`, and optional `QREDEX_ENVIRONMENT`, then delegates to `Qredex.init()`.

Create one shared `Qredex` instance per process. It is thread-safe and manages its own token cache.

## Public API

```java
// Creators
qredex.creators().create(request);
qredex.creators().get(creatorId);
qredex.creators().list(filters);

// Links
qredex.links().create(request);
qredex.links().get(linkId);
qredex.links().list(filters);
qredex.links().getStats(linkId);

// Intents
qredex.intents().issueInfluenceIntentToken(request);
qredex.intents().lockPurchaseIntent(request);

// Orders
qredex.orders().recordPaidOrder(request);
qredex.orders().list(filters);
qredex.orders().getDetails(orderAttributionId);

// Refunds
qredex.refunds().recordRefund(request);

// Auth (explicit — normally automatic)
qredex.auth().issueToken();
qredex.auth().clearTokenCache();
```

## Resource Capability Table

| Resource | Methods | Typical scopes |
|---|---|---|
| `creators()` | `create`, `get`, `list` | `CREATORS_WRITE`, `CREATORS_READ` |
| `links()` | `create`, `get`, `list`, `getStats` | `LINKS_WRITE`, `LINKS_READ` |
| `intents()` | `issueInfluenceIntentToken`, `lockPurchaseIntent` | `INTENTS_WRITE` |
| `orders()` | `recordPaidOrder`, `list`, `getDetails` | `ORDERS_WRITE`, `ORDERS_READ` |
| `refunds()` | `recordRefund` | `ORDERS_WRITE` |

## Environment Variables

| Variable | Required | Description |
|---|---|---|
| `QREDEX_CLIENT_ID` | Yes | Integration client ID |
| `QREDEX_CLIENT_SECRET` | Yes | Integration client secret |
| `QREDEX_ENVIRONMENT` | No | `production` or `staging` |
| `QREDEX_SCOPE` | No | Space-delimited OAuth scope string |

Use `QREDEX_ENVIRONMENT` only when you need `staging`. Most integrations can omit it.
For local testing against a non-Qredex base URL, use `Qredex.builder().baseUrl(...)` instead of treating localhost as a platform environment.

## Scopes

The SDK supports typed scope enums for explicit permission control:

```java
Qredex qredex = Qredex.builder()
    .clientId("...")
    .clientSecret("...")
    .scopes(QredexScope.LINKS_WRITE, QredexScope.INTENTS_WRITE, QredexScope.ORDERS_WRITE)
    .build();
```

When no scope is specified, the server assigns the default scope for your client credentials. Most integrations can omit explicit scopes.

Available scopes: `API`, `LINKS_READ`, `LINKS_WRITE`, `CREATORS_READ`, `CREATORS_WRITE`, `ORDERS_READ`, `ORDERS_WRITE`, `INTENTS_READ`, `INTENTS_WRITE`.

## Configuration Options

| Option | Default | Description |
|---|---|---|
| `clientId` | — | **Required.** Integration client ID |
| `clientSecret` | — | **Required.** Integration client secret |
| `environment` | `PRODUCTION` | Target environment |
| `baseUrl` | env default | Override base URL (useful in tests) |
| `timeoutMs` | `10000` | HTTP timeout in milliseconds |
| `maxAuthRetries` | `3` | Retry attempts for auth token issuance |
| `userAgentSuffix` | `null` | Appended to the `User-Agent` header |
| `scope` / `scopes` | `null` | OAuth scope — accepts a raw string or typed `QredexScope` enum values |
| `logger` | `null` | `QredexLogger` implementation for diagnostic logging |

## Auth

Auth is automatic. The SDK fetches a client-credentials token on the first call and caches it until close to expiry. You do not need to manage tokens.

For explicit control on the same client instance:

```java
// Warm the cache or inspect the issued token
OAuthTokenResponse token = qredex.auth().issueToken();
System.out.println(token.getExpiresIn());

// Force a fresh token on the next API call
qredex.auth().clearTokenCache();
```

## Idempotency

Qredex write operations achieve idempotency through stable external identifiers:

- **Paid orders** are idempotent on `(store_id, external_order_id)`.
- **Refunds** are idempotent on `(store_id, external_refund_id)`.

Safe to retry on timeout — submitting the same external IDs produces an `IDEMPOTENT` decision (HTTP 200), not a duplicate.

```java
// Safe to retry: same (store_id, external_order_id) is idempotent
RecordPaidOrderRequest request = RecordPaidOrderRequest.builder()
    .storeId(storeId)
    .externalOrderId("order-100045")  // stable external ID = idempotency key
    .currency("USD")
    .totalPrice(110.00)
    .purchaseIntentToken(pitToken)
    .build();

qredex.orders().recordPaidOrder(request);
```

Use stable, deterministic external IDs from your platform. Do not generate random IDs per retry attempt.

## Retry Behavior

- auth token issuance retries internally with exponential backoff
- read operations (`GET`) are not retried by default — configure `maxAuthRetries` only
- writes are never retried automatically
- when the server sends a `Retry-After` header (HTTP 429), the value is available on `QredexRateLimitException.getRetryAfterSeconds()`

## Error Handling

```java
import com.qredex.exceptions.*;

try {
    qredex.orders().recordPaidOrder(request);
} catch (QredexConflictException e) {
    // errorCode is "REJECTED_SOURCE_POLICY" or "REJECTED_CROSS_SOURCE_DUPLICATE"
    System.err.println("Conflict: " + e.getErrorCode() + " requestId=" + e.getRequestId());
} catch (QredexValidationException e) {
    System.err.println("Validation: " + e.getMessage());
} catch (QredexAuthenticationException e) {
    System.err.println("Auth failed — check credentials");
} catch (QredexRateLimitException e) {
    System.err.println("Rate limited — retry after " + e.getRetryAfterSeconds() + "s");
} catch (QredexNetworkException e) {
    System.err.println("Network error: " + e.getMessage());
} catch (QredexApiException e) {
    System.err.println("API error " + e.getStatus() + ": " + e.getMessage()
        + " traceId=" + e.getTraceId());
}
```

All exceptions extend `QredexException`. `QredexApiException` and its subclasses carry:

- `getStatus()` — HTTP status code
- `getErrorCode()` — canonical Qredex error code string
- `getRequestId()` — `X-Request-Id` from the response
- `getTraceId()` — `X-Trace-Id` from the response

See [docs/ERRORS.md](docs/ERRORS.md) for the full exception hierarchy and ingestion decision table.

## Canonical Flow

The shortest safe backend path is:

1. Create a creator.
2. Create a link for a store and creator.
3. Issue an IIT for the backend click event.
4. Lock the IIT into a PIT at cart time.
5. Record the paid order with the PIT.
6. Record refunds later with a stable external refund ID.

## Docs

- [Integration Guide](docs/INTEGRATION_GUIDE.md)
- [Error Handling](docs/ERRORS.md)

## Examples

- [AuthAndCreateCreator.java](examples/AuthAndCreateCreator.java)
- [CreateLink.java](examples/CreateLink.java)
- [IssueIit.java](examples/IssueIit.java)
- [LockPit.java](examples/LockPit.java)
- [RecordPaidOrder.java](examples/RecordPaidOrder.java)
- [ListOrders.java](examples/ListOrders.java)
- [GetOrderDetails.java](examples/GetOrderDetails.java)
- [RecordRefund.java](examples/RecordRefund.java)
- [CanonicalFlow.java](examples/CanonicalFlow.java)

## Testing

`mvn test` runs unit and WireMock-based HTTP integration tests only. No network access is required.

Live integration tests are opt-in and skipped unless `QREDEX_LIVE_ENABLED=1` and the required credentials are set. Tag: `@Tag("live")`.

```bash
# Run all tests
mvn test

# Run full build including tests
mvn clean verify
```

## Scope

This SDK covers the **Integrations API only**:

- `POST /api/v1/auth/token`
- `/api/v1/integrations/creators/**`
- `/api/v1/integrations/links/**`
- `/api/v1/integrations/intents/**`
- `/api/v1/integrations/orders/**`

It does **not** include:

- `/api/v1/merchant/**` — Merchant dashboard API
- `/api/v1/internal/**` — internal admin API
- Shopify OAuth or embedded app flows
- browser/runtime agent logic
- webhook receiver frameworks

## License

[Apache-2.0](LICENSE)

## Qredex Contact

- Website: https://qredex.com
- X: https://x.com/qredex
- Email: os@qredex.com
