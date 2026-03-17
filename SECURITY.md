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

# Security Policy

## Reporting a Vulnerability

If you discover a security vulnerability in the Qredex Java SDK, please report it responsibly.

**Do not open a public GitHub issue for security vulnerabilities.**

Email: **security@qredex.com**

Include:
- Description of the vulnerability
- Steps to reproduce
- Impact assessment
- Suggested fix (if any)

We will acknowledge receipt within 48 hours and provide a timeline for resolution.

## Supported Versions

| Version | Supported |
|---------|-----------|
| 0.1.x   | ✅ Current |

## Scope

This policy covers the `com.qredex:sdk` Java artifact and its source code in this repository.

It does not cover:
- The Qredex API itself (report API vulnerabilities to security@qredex.com separately)
- Third-party dependencies (report to their maintainers directly)

## Security Design

The SDK is designed with these security principles:

- **Secrets are never logged.** Client IDs, client secrets, and raw tokens are always redacted.
- **Integrations API only.** No merchant or internal admin endpoints are exposed.
- **Token caching is in-memory only.** Tokens are never written to disk.
- **Writes are not retried automatically.** Only auth token fetches are retried with backoff.
