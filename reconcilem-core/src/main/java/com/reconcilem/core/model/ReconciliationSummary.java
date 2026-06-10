package com.reconcilem.core.model;

public record ReconciliationSummary(
        int sourceCount,
        int targetCount,
        int matchedCount,
        int possibleMatchCount,
        int unmatchedSourceCount,
        int unmatchedTargetCount
) {

    public ReconciliationSummary {
        if (
                sourceCount < 0 ||
                        targetCount < 0 ||
                        matchedCount < 0 ||
                        possibleMatchCount < 0 ||
                        unmatchedSourceCount < 0 ||
                        unmatchedTargetCount < 0
        ) {
            throw new IllegalArgumentException("Summary counts must not be negative");
        }
    }
}