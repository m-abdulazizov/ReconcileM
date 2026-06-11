package com.reconcilem.core.model;

import java.util.List;
import java.util.Objects;

public record ReconciliationResult(
        List<MatchResult> matched,
        List<MatchResult> possibleMatches,
        List<MatchResult> duplicateMatches,
        List<MatchResult> conflictMatches,
        List<ReconciliationRecord> unmatchedSourceRecords,
        List<ReconciliationRecord> unmatchedTargetRecords,
        ReconciliationSummary summary
){

    public ReconciliationResult(
            List<MatchResult> matched,
            List<MatchResult> possibleMatches,
            List<ReconciliationRecord> unmatchedSourceRecords,
            List<ReconciliationRecord> unmatchedTargetRecords,
            ReconciliationSummary summary
    ){
        this(
                matched,
                possibleMatches,
                List.of(),
                List.of(),
                unmatchedSourceRecords,
                unmatchedTargetRecords,
                summary
        );
    }

    public ReconciliationResult {
        matched = matched == null ? List.of() : List.copyOf(matched);
        possibleMatches = possibleMatches == null ? List.of() : List.copyOf(possibleMatches);
        duplicateMatches = duplicateMatches == null ? List.of() : List.copyOf(duplicateMatches);
        conflictMatches = conflictMatches == null ? List.of() : List.copyOf(conflictMatches);
        unmatchedSourceRecords = unmatchedSourceRecords == null ? List.of() : List.copyOf(unmatchedSourceRecords);
        unmatchedTargetRecords = unmatchedTargetRecords == null ? List.of() : List.copyOf(unmatchedTargetRecords);
        Objects.requireNonNull(summary, "Reconciliation summary must not be null");
    }
}