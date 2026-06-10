package com.reconcilem.core.engine;

import com.reconcilem.core.exception.ReconcileMException;
import com.reconcilem.core.model.MatchDecision;
import com.reconcilem.core.model.MatchResult;
import com.reconcilem.core.model.MatchScore;
import com.reconcilem.core.model.ReconciliationJob;
import com.reconcilem.core.model.ReconciliationRecord;
import com.reconcilem.core.model.ReconciliationResult;
import com.reconcilem.core.model.ReconciliationSummary;
import com.reconcilem.core.rule.MatchingRule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DefaultReconciliationEngine implements ReconciliationEngine {

    @Override
    public ReconciliationResult reconcile(
            List<ReconciliationRecord> sourceRecords,
            List<ReconciliationRecord> targetRecords,
            ReconciliationJob job
    ) {
        validateInput(sourceRecords, targetRecords, job);

        List<MatchResult> matched = new ArrayList<>();
        List<MatchResult> possibleMatches = new ArrayList<>();
        List<ReconciliationRecord> unmatchedSourceRecords = new ArrayList<>();
        Set<String> candidateTargetIds = new HashSet<>();

        for (ReconciliationRecord sourceRecord : sourceRecords) {
            MatchResult bestResult = findBestMatch(sourceRecord, targetRecords, job);

            if (bestResult == null) {
                unmatchedSourceRecords.add(sourceRecord);
                continue;
            }

            if (bestResult.decision() == MatchDecision.MATCHED) {
                matched.add(bestResult);
                candidateTargetIds.add(bestResult.targetRecord().id());
            } else if (bestResult.decision() == MatchDecision.POSSIBLE_MATCH) {
                possibleMatches.add(bestResult);
                candidateTargetIds.add(bestResult.targetRecord().id());
            } else {
                unmatchedSourceRecords.add(sourceRecord);
            }
        }

        List<ReconciliationRecord> unmatchedTargetRecords = targetRecords.stream()
                .filter(target -> !candidateTargetIds.contains(target.id()))
                .toList();

        ReconciliationSummary summary = new ReconciliationSummary(
                sourceRecords.size(),
                targetRecords.size(),
                matched.size(),
                possibleMatches.size(),
                unmatchedSourceRecords.size(),
                unmatchedTargetRecords.size()
        );

        return new ReconciliationResult(
                matched,
                possibleMatches,
                unmatchedSourceRecords,
                unmatchedTargetRecords,
                summary
        );
    }

    private MatchResult findBestMatch(
            ReconciliationRecord sourceRecord,
            List<ReconciliationRecord> targetRecords,
            ReconciliationJob job
    ) {
        MatchResult bestResult = null;

        for (ReconciliationRecord targetRecord : targetRecords) {
            MatchResult result = compare(sourceRecord, targetRecord, job);

            if (bestResult == null || result.totalScore() > bestResult.totalScore()) {
                bestResult = result;
            }
        }

        return bestResult;
    }

    private MatchResult compare(
            ReconciliationRecord sourceRecord,
            ReconciliationRecord targetRecord,
            ReconciliationJob job
    ) {
        List<MatchScore> scores = new ArrayList<>();
        int totalScore = 0;

        for (MatchingRule rule : job.rules()) {
            try {
                MatchScore score = rule.evaluate(sourceRecord, targetRecord);
                scores.add(score);
                totalScore += score.score();
            } catch (Exception ex) {
                throw new ReconcileMException(
                        "Failed to execute matching rule: " + rule.name(),
                        ex
                );
            }
        }

        MatchDecision decision = decide(totalScore, job);

        return new MatchResult(
                sourceRecord,
                targetRecord,
                totalScore,
                decision,
                scores
        );
    }

    private MatchDecision decide(int totalScore, ReconciliationJob job) {
        if (totalScore >= job.thresholds().matchedScore()) {
            return MatchDecision.MATCHED;
        }

        if (totalScore >= job.thresholds().possibleMatchScore()) {
            return MatchDecision.POSSIBLE_MATCH;
        }

        return MatchDecision.UNMATCHED;
    }

    private void validateInput(
            List<ReconciliationRecord> sourceRecords,
            List<ReconciliationRecord> targetRecords,
            ReconciliationJob job
    ) {
        Objects.requireNonNull(sourceRecords, "Source records must not be null");
        Objects.requireNonNull(targetRecords, "Target records must not be null");
        Objects.requireNonNull(job, "Reconciliation job must not be null");
    }
}