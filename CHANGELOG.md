<!--
   Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
   Licensed under the Apache License, Version 2.0.
-->

# Changelog

All notable changes to the Qredex Java SDK are documented in this file.

This project adheres to [Semantic Versioning](https://semver.org/).

## [0.1.0] — 2026-03-17

### Added
- Initial SDK release
- `Qredex.builder()`, `Qredex.init()`, `Qredex.bootstrap()` initialization
- `QredexScope` enum for typed OAuth scope control
- Resource clients: `creators()`, `links()`, `intents()`, `orders()`, `refunds()`
- Creator operations: create, get, list
- Link operations: create, get, list, getStats
- Intent operations: issueInfluenceIntentToken, lockPurchaseIntent
- Order operations: recordPaidOrder, list, getDetails
- Refund operations: recordRefund
- Automatic client-credentials auth with token caching and proactive refresh
- Structured exception hierarchy with `requestId`, `traceId`, and `errorCode`
- Forward-compatible enum deserialization with `UNKNOWN` sentinels
- `Closeable` support for clean shutdown
- `QredexLogger` interface for sanitized diagnostic logging
- WireMock-based test suite (31 tests)
- Integration guide, error reference, and canonical flow examples
