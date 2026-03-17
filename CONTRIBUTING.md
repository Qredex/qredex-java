<!--
   Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
   Licensed under the Apache License, Version 2.0.
-->

# Contributing to the Qredex Java SDK

Thank you for your interest in contributing to the Qredex Java SDK.

## Before You Start

- Check [existing issues](https://github.com/Qredex/qredex-java/issues) before opening a new one.
- For large changes, open an issue first to discuss the approach.
- This SDK covers the **Integrations API only**. Changes that expand scope beyond Integrations will not be accepted.

## Development Setup

### Requirements

- Java 8+
- Maven 3.6+

### Build and test

```bash
mvn clean test        # run tests
mvn clean verify      # full build + tests
```

Tests use WireMock for HTTP mocking. No network access or API credentials are needed for local development.

## Pull Request Guidelines

1. **One change per PR.** Keep pull requests focused on a single theme.
2. **Include tests.** Bug fixes must include a regression test. New features must include happy path and failure path coverage.
3. **Follow existing patterns.** Match the code style, naming conventions, and architecture of the existing codebase.
4. **Update docs.** If your change affects the public API, update README.md and relevant docs.
5. **No unused code.** Do not add classes, methods, or imports that are not actually used.
6. **Run tests before submitting.** `mvn clean test` must pass with zero failures.

## Code Style

- Java 8 syntax only (no `var`, `record`, `sealed`, text blocks)
- Standard Java naming conventions (PascalCase classes, camelCase methods)
- Builders for complex object construction
- `QredexValidationException` for validation failures (not `IllegalStateException`)
- `@JsonIgnoreProperties(ignoreUnknown = true)` on all response DTOs
- Preserve canonical Qredex naming (IIT, PIT, `token_integrity`, `resolution_status`)

## License

By contributing, you agree that your contributions will be licensed under the [Apache License 2.0](LICENSE).
