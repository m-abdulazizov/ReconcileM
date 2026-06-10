package com.reconcilem.core.model;

public record ReconciliationThresholds(
        int matchedScore,
        int possibleMatchScore
) {
    public ReconciliationThresholds {
        if (matchedScore < 0 || possibleMatchScore < 0) {
            throw new IllegalArgumentException("Threshold scores must not be negative");
        }
        if (possibleMatchScore > matchedScore) {
            throw new IllegalArgumentException("Possible match score must not be greater than matched score");
        }
    }

    public static ReconciliationThresholds defaults () {
        return new ReconciliationThresholds(80, 50);
    }
}