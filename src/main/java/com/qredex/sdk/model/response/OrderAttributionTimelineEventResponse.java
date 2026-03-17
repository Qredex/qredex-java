/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** A single event in the order attribution timeline. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class OrderAttributionTimelineEventResponse {

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("occurred_at")
    private String occurredAt;

    public String getEventType() { return eventType; }
    public String getOccurredAt() { return occurredAt; }

    @Override
    public String toString() {
        return "OrderAttributionTimelineEventResponse{eventType='" + eventType + "', occurredAt='" + occurredAt + "'}";
    }
}
