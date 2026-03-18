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
package com.qredex.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qredex.model.standards.*;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
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

    @Nullable
    public Integer getScoreVersion() { return scoreVersion; }
    @Nullable
    public Integer getBaseScore() { return baseScore; }
    @Nullable
    public Integer getOriginAdjustment() { return originAdjustment; }
    @Nullable
    public Integer getDuplicateAdjustment() { return duplicateAdjustment; }
    @Nullable
    public Integer getFinalScore() { return finalScore; }
    @Nullable
    public TokenIntegrity getTokenIntegrity() { return tokenIntegrity; }
    @Nullable
    public IntegrityReason getIntegrityReason() { return integrityReason; }
    @Nullable
    public WindowStatus getWindowStatus() { return windowStatus; }
    @Nullable
    public ResolutionStatus getResolutionStatus() { return resolutionStatus; }
    @Nullable
    public OriginMatchStatus getOriginMatchStatus() { return originMatchStatus; }
    @Nullable
    public DuplicateConfidence getDuplicateConfidence() { return duplicateConfidence; }
    @Nullable
    public Boolean getReviewRequired() { return reviewRequired; }
    @Nullable
    public List<String> getReviewReasons() { return reviewReasons == null ? null : Collections.unmodifiableList(reviewReasons); }

    @Override
    public String toString() {
        return "OrderAttributionScoreBreakdownResponse{scoreVersion=" + scoreVersion
                + ", baseScore=" + baseScore + ", originAdjustment=" + originAdjustment
                + ", duplicateAdjustment=" + duplicateAdjustment + ", finalScore=" + finalScore
                + ", tokenIntegrity=" + tokenIntegrity + ", integrityReason=" + integrityReason
                + ", windowStatus=" + windowStatus + ", resolutionStatus=" + resolutionStatus
                + ", originMatchStatus=" + originMatchStatus + ", duplicateConfidence=" + duplicateConfidence
                + ", reviewRequired=" + reviewRequired + "}";
    }
}
