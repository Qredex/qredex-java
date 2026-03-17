<!--
     ▄▄▄▄
   ▄█▀▀███▄▄              █▄
   ██    ██ ▄             ██
   ██    ██ ████▄▄█▀█▄ ▄████ ▄█▀█▄▀██ ██▀
   ██  ▄ ██ ██   ██▄█▀ ██ ██ ██▄█▀  ███
    ▀█████▄▄█▀  ▄▀█▄▄▄▄█▀███▄▀█▄▄▄▄██ ██▄
         ▀█

   Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
   Licensed under the Apache License, Version 2.0.
-->

# Qredex Java Server SDK

Official Java server SDK for Qredex machine-to-machine integrations.

## Requirements

- Java 8+
- Maven 3.6+

## Installation

```xml
<dependency>
    <groupId>com.qredex</groupId>
    <artifactId>sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick start

```java
// Option A: bootstrap from environment variables
// Reads QREDEX_CLIENT_ID, QREDEX_CLIENT_SECRET, and optionally QREDEX_ENVIRONMENT
Qredex qredex = Qredex.bootstrap();

// Option B: explicit configuration
Qredex qredex = Qredex.builder()
    .clientId("your-client-id")
    .clientSecret("your-client-secret")
    .environment(QredexEnvironment.PRODUCTION)
    .build();
```

## Canonical integration flow

```java
// 1. Create a creator
CreatorResponse creator = qredex.creators().create(
    CreateCreatorRequest.builder()
        .handle("alice")
        .displayName("Alice")
        .build());

// 2. Create a link
LinkResponse link = qredex.links().create(
    CreateLinkRequest.builder()
        .storeId(storeId)
        .creatorId(creator.getId())
        .linkName("spring-launch")
        .destinationPath("/collections/spring")
        .attributionWindowDays(30)
        .build());

// 3. Issue an Influence Intent Token (IIT) — backend click flow
InfluenceIntentResponse iit = qredex.intents().issueInfluenceIntentToken(
    IssueInfluenceIntentTokenRequest.builder()
        .linkId(link.getId())
        .referrer("https://instagram.com/alice")
        .build());

// 4. Lock a Purchase Intent Token (PIT) — at add-to-cart
PurchaseIntentResponse pit = qredex.intents().lockPurchaseIntent(
    LockPurchaseIntentRequest.builder()
        .token(iit.getToken())
        .source("backend-cart")
        .build());

// 5. Record the paid order
OrderAttributionResponse order = qredex.orders().recordPaidOrder(
    RecordPaidOrderRequest.builder()
        .storeId(storeId)
        .externalOrderId("order-100045")
        .currency("USD")
        .totalPrice(110.00)
        .purchaseIntentToken(pit.getToken())   // ← key for attribution
        .build());

// 6. Record a refund
qredex.refunds().recordRefund(
    RecordRefundRequest.builder()
        .storeId(storeId)
        .externalOrderId("order-100045")
        .externalRefundId("refund-987")
        .refundTotal(110.00)
        .build());
```

## API surface

| Accessor | Methods |
|----------|---------|
| `qredex.creators()` | `create`, `get`, `list` |
| `qredex.links()` | `create`, `get`, `list`, `getStats` |
| `qredex.intents()` | `issueInfluenceIntentToken`, `lockPurchaseIntent` |
| `qredex.orders()` | `recordPaidOrder`, `list`, `getDetails` |
| `qredex.refunds()` | `recordRefund` |
| `qredex.auth()` | `issueToken`, `clearTokenCache` |

## Configuration options

| Option | Default | Description |
|--------|---------|-------------|
| `clientId` | — | **Required.** Integration client ID |
| `clientSecret` | — | **Required.** Integration client secret |
| `environment` | `PRODUCTION` | API environment |
| `baseUrl` | env default | Override base URL |
| `timeoutMs` | `10000` | HTTP timeout in ms |
| `maxAuthRetries` | `3` | Auth retry attempts |
| `userAgentSuffix` | `null` | Appended to User-Agent |
| `logger` | `null` | `QredexLogger` implementation |

## Environment variables (for `Qredex.bootstrap()`)

| Variable | Required | Description |
|----------|----------|-------------|
| `QREDEX_CLIENT_ID` | Yes | Integration client ID |
| `QREDEX_CLIENT_SECRET` | Yes | Integration client secret |
| `QREDEX_ENVIRONMENT` | No | `production`, `staging`, or `development` |
| `QREDEX_SCOPE` | No | OAuth scope string |

## Auth

Auth is managed automatically. The SDK fetches a client-credentials token on the first API call and caches it for its full lifetime. You do not need to manage tokens.

For explicit control:

```java
// Warm the token cache or inspect the issued token
OAuthTokenResponse token = qredex.auth().issueToken();

// Force a fresh token on the next call
qredex.auth().clearTokenCache();
```

## Error handling

```java
import com.qredex.sdk.exceptions.*;

try {
    qredex.orders().recordPaidOrder(request);
} catch (QredexConflictException e) {
    // e.getErrorCode() = "REJECTED_SOURCE_POLICY" or "REJECTED_CROSS_SOURCE_DUPLICATE"
    System.err.println("Rejected: " + e.getErrorCode() + " requestId=" + e.getRequestId());
} catch (QredexValidationException e) {
    System.err.println("Validation: " + e.getMessage());
} catch (QredexAuthenticationException e) {
    System.err.println("Auth failed: " + e.getMessage());
} catch (QredexRateLimitException e) {
    System.err.println("Rate limited, retry after: " + e.getRetryAfterSeconds() + "s");
} catch (QredexNetworkException e) {
    System.err.println("Network error: " + e.getMessage());
}
```

See [docs/ERRORS.md](docs/ERRORS.md) for the full error model.

## Building from source

```bash
mvn clean install
mvn test
```

## Scope

This SDK covers the **Integrations API only**:

- `/api/v1/auth/token`
- `/api/v1/integrations/creators/**`
- `/api/v1/integrations/links/**`
- `/api/v1/integrations/intents/**`
- `/api/v1/integrations/orders/**`

It does **not** include the Merchant dashboard API, internal admin API, Shopify OAuth flows, or browser agent behavior.

## License

Apache License, Version 2.0. See [LICENSE](LICENSE).
