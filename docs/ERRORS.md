<!--
   Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
   Licensed under the Apache License, Version 2.0.
-->

# Qredex Java SDK — Error Reference

All SDK errors extend `QredexException` (a `RuntimeException`). The hierarchy maps directly to HTTP status codes and API error semantics.

---

## Exception hierarchy

```
QredexException
├── QredexConfigurationException    // invalid SDK config, before any HTTP
├── QredexNetworkException          // transport failure, no HTTP response
└── QredexApiException              // API returned an error response
    ├── QredexAuthenticationException  // HTTP 401
    ├── QredexAuthorizationException   // HTTP 403
    ├── QredexValidationException      // HTTP 400 (or client-side pre-flight)
    ├── QredexNotFoundException        // HTTP 404
    ├── QredexConflictException        // HTTP 409
    ├── QredexRateLimitException       // HTTP 429
    └── QredexApiException             // all other 4xx/5xx
```

---

## Common fields on all API exceptions

| Method | Type | Description |
|--------|------|-------------|
| `getMessage()` | `String` | Human-readable error message |
| `getStatus()` | `Integer` | HTTP status code |
| `getErrorCode()` | `String` | Qredex canonical `error_code` |
| `getRequestId()` | `String` | `X-Request-Id` header value |
| `getTraceId()` | `String` | `X-Trace-Id` header value |
| `getResponseBody()` | `String` | Raw API response body |

---

## Exception types

### `QredexConfigurationException`

Thrown before any network activity when SDK config is invalid.

```java
Qredex.builder().build(); // → QredexConfigurationException: clientId required
```

### `QredexNetworkException`

Thrown when a network or transport error occurs with no HTTP response (e.g., DNS failure, connection timeout, SSL error).

```java
try {
    qredex.creators().list();
} catch (QredexNetworkException e) {
    log.error("Network error: " + e.getMessage(), e.getCause());
}
```

### `QredexAuthenticationException` (HTTP 401)

Invalid or missing credentials. Most common causes:
- Incorrect `clientId` or `clientSecret`
- Expired or revoked access token
- Wrong `grant_type`

```java
} catch (QredexAuthenticationException e) {
    log.error("Auth failed: " + e.getErrorCode()); // e.g. "invalid_credentials"
}
```

### `QredexAuthorizationException` (HTTP 403)

Authenticated, but missing required scope or permission.

```java
} catch (QredexAuthorizationException e) {
    log.error("Forbidden: " + e.getErrorCode()); // e.g. "insufficient_scope"
}
```

### `QredexValidationException` (HTTP 400)

Invalid request payload. Also thrown client-side for pre-flight validation failures (e.g., blank required field).

```java
} catch (QredexValidationException e) {
    log.error("Validation failed: " + e.getMessage());
    // e.getStatus() == null for client-side failures
    // e.getStatus() == 400 for server-side failures
}
```

### `QredexNotFoundException` (HTTP 404)

The requested resource (creator, link, order) does not exist.

```java
} catch (QredexNotFoundException e) {
    log.warn("Not found: requestId=" + e.getRequestId());
}
```

### `QredexConflictException` (HTTP 409)

A policy or duplicate conflict rejected the request. This is the canonical rejection path for order and refund ingestion.

**Critical:** Do not treat this as a generic error. Inspect `getErrorCode()`:

| `error_code` | Meaning |
|-------------|---------|
| `REJECTED_SOURCE_POLICY` | Merchant source policy disallows this order source |
| `REJECTED_CROSS_SOURCE_DUPLICATE` | Same order already exists under a different source |

```java
} catch (QredexConflictException e) {
    if ("REJECTED_SOURCE_POLICY".equals(e.getErrorCode())) {
        // order source not permitted for this merchant
    } else if ("REJECTED_CROSS_SOURCE_DUPLICATE".equals(e.getErrorCode())) {
        // order exists under a conflicting source
    }
    log.warn("Ingestion rejected: " + e.getErrorCode() + " requestId=" + e.getRequestId());
}
```

### `QredexRateLimitException` (HTTP 429)

Rate limit exceeded. Implements back-off based on the `Retry-After` response header.

```java
} catch (QredexRateLimitException e) {
    Long retryAfter = e.getRetryAfterSeconds(); // null if header absent
    if (retryAfter != null) {
        Thread.sleep(retryAfter * 1000L);
        // retry
    }
}
```

---

## Handling example

```java
try {
    OrderAttributionResponse order = qredex.orders().recordPaidOrder(request);
    System.out.println("Ingested: " + order.getResolutionStatus());

} catch (QredexConflictException e) {
    System.err.println("Rejected [" + e.getErrorCode() + "]: " + e.getMessage());
    System.err.println("requestId=" + e.getRequestId());

} catch (QredexValidationException e) {
    System.err.println("Invalid request: " + e.getMessage());

} catch (QredexAuthenticationException e) {
    System.err.println("Check your credentials: " + e.getErrorCode());

} catch (QredexRateLimitException e) {
    System.err.println("Rate limited. Retry after " + e.getRetryAfterSeconds() + "s");

} catch (QredexNetworkException e) {
    System.err.println("Network error: " + e.getMessage());

} catch (QredexApiException e) {
    System.err.println("API error HTTP " + e.getStatus() + ": " + e.getMessage());
}
```

---

## Idempotent ingestion vs. conflicts

The Qredex ingestion API distinguishes these outcomes:

| Decision | HTTP | Exception | Notes |
|----------|------|-----------|-------|
| `INGESTED` | 200/201 | None | First write — new record |
| `IDEMPOTENT` | 200/201 | None | Safe replay — already recorded |
| `REJECTED_SOURCE_POLICY` | 409 | `QredexConflictException` | Policy disallowed |
| `REJECTED_CROSS_SOURCE_DUPLICATE` | 409 | `QredexConflictException` | Cross-source duplicate |

Treat `INGESTED` and `IDEMPOTENT` as success. Handle `REJECTED_*` explicitly.

---

## Canonical Qredex fields

These fields appear on order attribution responses and carry domain-specific meaning. Do not collapse them into boolean helpers.

| Field | Type | Values |
|-------|------|--------|
| `resolution_status` | `ResolutionStatus` | `ATTRIBUTED`, `UNATTRIBUTED`, `REJECTED` |
| `token_integrity` | `TokenIntegrity` | `VALID`, `INVALID` |
| `integrity_reason` | `IntegrityReason` | `MISSING`, `TAMPERED`, `EXPIRED`, `MISMATCHED`, `REPLACED`, `LINK_INACTIVE`, `CREATOR_INACTIVE` |
| `integrity_band` | `IntegrityBand` | `HIGH`, `MEDIUM`, `LOW`, `CRITICAL` |
| `window_status` | `WindowStatus` | `WITHIN`, `OUTSIDE`, `UNKNOWN` |
| `origin_match_status` | `OriginMatchStatus` | `MATCH`, `MISMATCH`, `ABSENT`, `UNKNOWN` |
