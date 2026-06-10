package com.reconcilem.core.model;

import java.util.Objects;

public record MatchScore(
        String ruleName,
        int score,
        String explanation
){
    public MatchScore{
        Objects.requireNonNull(ruleName, "Rule Name must not be null");

        if (score < 0){
            throw new IllegalArgumentException("Match score must not be negative");
        }
        explanation = explanation == null ? "" : explanation;
    }
}