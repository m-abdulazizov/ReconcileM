package com.reconcilem.core.model;

import java.util.List;
import java.util.Objects;

public record MatchResult(
        ReconciliationRecord sourceRecord,
        ReconciliationRecord targetRecord,

        int totalScore,
        MatchDecision decision,
        List<MatchScore> scores
)
{
    public MatchResult{
        Objects.requireNonNull(sourceRecord, "Source record must not be null");
        Objects.requireNonNull(decision, "Match decision must not be null");

        scores = scores == null ? List.of() : List.copyOf(scores);

        if (totalScore < 0) {
            throw new IllegalArgumentException("Total score must not be negative");
        }
    }
}