/*
 *  Copyright (C) 2026 — 2026, Qredex, LTD. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0.
 */
package com.qredex.sdk.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qredex.sdk.model.enums.*;
import java.util.List;

/** Attribution integrity score breakdown, nested inside {@link OrderAttributionDetailsResponse}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class OrderAttributionScoreBreakdownResponse {

    @JsonProperty("score_version")
    private Integer scoreVersion;

    @JsonProperty("base_score")
    private Integer baseScore;

    @JsonProperty("origin_adjustment")
    private Integer originAdjustment;

    @JsonProperty("duplicate_adjustment")
    private Integer duplicateAdjustment;

    @JsonProperty("final_score")
    private Integer finalScore;

    @JsonProperty("token_integrity")
    private TokenIntegrity tokenIntegrity;

    @JsonProperty("integrity_reason")
    private IntegrityReason integrityReason;

    @JsonProperty("window_status")
    private WindowStatus windowStatus;

    @JsonProperty("resolution_status")
    private ResolutionStatus resolutionStatus;

    @JsonProperty("origin_match_status")
    private OriginMatchStatus originMatchStatus;

    @JsonProperty("duplicate_confidence")
    private DuplicateConfidence duplicateConfidence;

    @JsonProperty("review_required")
    private Boolean reviewRequired;

    @JsonProperty("review_reasons")
    private List<String> reviewReasons;

    public Integer getScoreVersion() { return scoreVersion; }
    public Integer getBaseScore() { return baseScore; }
    public Integer getOriginAdjustment() { return originAdjustment; }
    public Integer getDuplicateAdjustment() { return duplicateAdjustment; }
    public Integer getFinalScore() { return finalScore; }
    public TokenIntegrity getTokenIntegrity() { return tokenIntegrity; }
    public IntegrityReason getIntegrityReason() { return integrityReason; }
    public WindowStatus getWindowStatus() { return windowStatus; }
    public ResolutionStatus getResolutionStatus() { return resolutionStatus; }
    public OriginMatchStatus getOriginMatchStatus() { return originMatchStatus; }
    public DuplicateConfidence getDuplicateConfidence() { return duplicateConfidence; }
    public Boolean getReviewRequired() { return reviewRequired; }
    public List<String> getReviewReasons() { return reviewReasons; }
}
