/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk;

/**
 * Minimal logger interface for sanitized Qredex SDK diagnostics.
 *
 * <p>Implement this interface to route SDK log output to your application logger
 * (SLF4J, Log4j, java.util.logging, etc.).
 *
 * <p><strong>Secrets are never passed to this interface.</strong>
 * Client IDs, client secrets, and raw tokens are always redacted before logging.
 *
 * <pre>{@code
 * QredexConfig config = QredexConfig.builder()
 *     .clientId(clientId)
 *     .clientSecret(clientSecret)
 *     .logger(new QredexLogger() {
 *         public void debug(String message) { log.debug("[qredex] " + message); }
 *         public void info(String message)  { log.info("[qredex] "  + message); }
 *         public void warn(String message)  { log.warn("[qredex] "  + message); }
 *         public void error(String message) { log.error("[qredex] " + message); }
 *     })
 *     .build();
 * }</pre>
 */
public interface QredexLogger {
    void debug(String message);
    void info(String message);
    void warn(String message);
    void error(String message);

    /**
     * Logs an error message with a cause. Override this to pass stack traces to your logger.
     * The default implementation delegates to {@link #error(String)}.
     */
    default void error(String message, Throwable cause) {
        error(message);
    }
}
