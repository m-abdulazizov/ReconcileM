package com.reconcilem.test;

import com.reconcilem.core.model.MatchDecision;
import com.reconcilem.core.model.MatchResult;
import com.reconcilem.core.model.ReconciliationResult;

import static org.assertj.core.api.Assertions.assertThat;

public final class MatchAssertions {

    private MatchAssertions() {
    }

    public static void assertMatched(ReconciliationResult result, String sourceRecordId, String targetRecordId) {
        assertThat(result.matched())
                .anySatisfy(match -> assertMatch(match, sourceRecordId, targetRecordId, MatchDecision.MATCHED));
    }

    public static void assertPossibleMatch(ReconciliationResult result, String sourceRecordId, String targetRecordId) {
        assertThat(result.possibleMatches())
                .anySatisfy(match -> assertMatch(match, sourceRecordId, targetRecordId, MatchDecision.POSSIBLE_MATCH));
    }

    public static void assertDuplicate(ReconciliationResult result, String sourceRecordId, String targetRecordId) {
        assertThat(result.duplicateMatches())
                .anySatisfy(match -> assertMatch(match, sourceRecordId, targetRecordId, MatchDecision.DUPLICATE));
    }

    public static void assertConflict(ReconciliationResult result, String sourceRecordId, String targetRecordId) {
        assertThat(result.conflictMatches())
                .anySatisfy(match -> assertMatch(match, sourceRecordId, targetRecordId, MatchDecision.CONFLICT));
    }

    public static void assertUnmatchedSource(ReconciliationResult result, String sourceRecordId) {
        assertThat(result.unmatchedSourceRecords())
                .anySatisfy(record -> assertThat(record.id()).isEqualTo(sourceRecordId));
    }

    public static void assertUnmatchedTarget(ReconciliationResult result, String targetRecordId) {
        assertThat(result.unmatchedTargetRecords())
                .anySatisfy(record -> assertThat(record.id()).isEqualTo(targetRecordId));
    }

    public static void assertSummary(
            ReconciliationResult result,
            int sourceCount,
            int targetCount,
            int matchedCount,
            int possibleMatchCount,
            int unmatchedSourceCount,
            int unmatchedTargetCount
    ) {
        assertThat(result.summary().sourceCount()).isEqualTo(sourceCount);
        assertThat(result.summary().targetCount()).isEqualTo(targetCount);
        assertThat(result.summary().matchedCount()).isEqualTo(matchedCount);
        assertThat(result.summary().possibleMatchCount()).isEqualTo(possibleMatchCount);
        assertThat(result.summary().unmatchedSourceCount()).isEqualTo(unmatchedSourceCount);
        assertThat(result.summary().unmatchedTargetCount()).isEqualTo(unmatchedTargetCount);
    }

    private static void assertMatch(
            MatchResult match,
            String sourceRecordId,
            String targetRecordId,
            MatchDecision decision
    ) {
        assertThat(match.sourceRecord().id()).isEqualTo(sourceRecordId);
        assertThat(match.targetRecord()).isNotNull();
        assertThat(match.targetRecord().id()).isEqualTo(targetRecordId);
        assertThat(match.decision()).isEqualTo(decision);
    }
}
