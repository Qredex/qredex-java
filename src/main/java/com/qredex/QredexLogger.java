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
package com.qredex;

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
