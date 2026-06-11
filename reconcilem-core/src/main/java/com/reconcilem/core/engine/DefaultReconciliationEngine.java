package com.reconcilem.core.engine;

import com.reconcilem.core.exception.ReconcileMException;
import com.reconcilem.core.model.MatchDecision;
import com.reconcilem.core.model.MatchResult;
import com.reconcilem.core.model.MatchScore;
import com.reconcilem.core.model.ReconciliationJob;
import com.reconcilem.core.model.ReconciliationRecord;
import com.reconcilem.core.model.ReconciliationResult;
import com.reconcilem.core.model.ReconciliationSummary;
import com.reconcilem.core.normalizer.RecordNormalizer;
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

        List<NormalizedRecord> normalizedSourceRecords = sourceRecords.stream()
                .map(record -> new NormalizedRecord(record, normalize(record, job)))
                .toList();

        List<NormalizedRecord> normalizedTargetRecords = targetRecords.stream()
                .map(record -> new NormalizedRecord(record, normalize(record, job)))
                .toList();

        List<MatchResult> matched = new ArrayList<>();
        List<MatchResult> possibleMatches = new ArrayList<>();
        List<MatchResult> duplicateMatches = new ArrayList<>();
        List<MatchResult> conflictMatches = new ArrayList<>();
        List<ReconciliationRecord> unmatchedSourceRecords = new ArrayList<>();

        Set<String> consumedTargetIds = new HashSet<>();

        for (NormalizedRecord sourceRecord : normalizedSourceRecords) {
            MatchResult bestOverallResult = findBestMatch(sourceRecord, normalizedTargetRecords, job);

            if (bestOverallResult == null || bestOverallResult.decision() == MatchDecision.UNMATCHED) {
                unmatchedSourceRecords.add(sourceRecord.original());
                continue;
            }

            String targetId = bestOverallResult.targetRecord().id();

            if (consumedTargetIds.contains(targetId)) {
                duplicateMatches.add(asDuplicate(bestOverallResult));
                unmatchedSourceRecords.add(sourceRecord.original());
                continue;
            }

            MatchResult bestAvailableResult = findBestAvailableMatch(
                    sourceRecord,
                    normalizedTargetRecords,
                    consumedTargetIds,
                    job
            );

            if (bestAvailableResult == null || bestAvailableResult.decision() == MatchDecision.UNMATCHED) {
                unmatchedSourceRecords.add(sourceRecord.original());
                continue;
            }

            if (!Objects.equals(bestOverallResult.targetRecord().id(), bestAvailableResult.targetRecord().id())) {
                conflictMatches.add(asConflict(bestOverallResult));
            }

            if (bestAvailableResult.decision() == MatchDecision.MATCHED) {
                matched.add(bestAvailableResult);
                consumedTargetIds.add(bestAvailableResult.targetRecord().id());
            } else if (bestAvailableResult.decision() == MatchDecision.POSSIBLE_MATCH) {
                possibleMatches.add(bestAvailableResult);
                consumedTargetIds.add(bestAvailableResult.targetRecord().id());
            } else {
                unmatchedSourceRecords.add(sourceRecord.original());
            }
        }

        List<ReconciliationRecord> unmatchedTargetRecords = targetRecords.stream()
                .filter(target -> !consumedTargetIds.contains(target.id()))
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
                duplicateMatches,
                conflictMatches,
                unmatchedSourceRecords,
                unmatchedTargetRecords,
                summary
        );
    }

    private MatchResult findBestMatch(
            NormalizedRecord sourceRecord,
            List<NormalizedRecord> targetRecords,
            ReconciliationJob job
    ) {
        MatchResult bestResult = null;

        for (NormalizedRecord targetRecord : targetRecords) {
            MatchResult result = compare(sourceRecord, targetRecord, job);

            if (bestResult == null || result.totalScore() > bestResult.totalScore()) {
                bestResult = result;
            }
        }

        return bestResult;
    }

    private MatchResult findBestAvailableMatch(
            NormalizedRecord sourceRecord,
            List<NormalizedRecord> targetRecords,
            Set<String> consumedTargetIds,
            ReconciliationJob job
    ) {
        MatchResult bestResult = null;

        for (NormalizedRecord targetRecord : targetRecords) {
            if (consumedTargetIds.contains(targetRecord.original().id())) {
                continue;
            }

            MatchResult result = compare(sourceRecord, targetRecord, job);

            if (bestResult == null || result.totalScore() > bestResult.totalScore()) {
                bestResult = result;
            }
        }

        return bestResult;
    }

    private MatchResult compare(
            NormalizedRecord sourceRecord,
            NormalizedRecord targetRecord,
            ReconciliationJob job
    ) {
        List<MatchScore> scores = new ArrayList<>();
        int totalScore = 0;

        for (MatchingRule rule : job.rules()) {
            try {
                MatchScore score = rule.evaluate(sourceRecord.normalized(), targetRecord.normalized());
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
                sourceRecord.original(),
                targetRecord.original(),
                totalScore,
                decision,
                scores
        );
    }

    private MatchResult asDuplicate(MatchResult result) {
        return new MatchResult(
                result.sourceRecord(),
                result.targetRecord(),
                result.totalScore(),
                MatchDecision.DUPLICATE,
                result.scores()
        );
    }

    private MatchResult asConflict(MatchResult result) {
        return new MatchResult(
                result.sourceRecord(),
                result.targetRecord(),
                result.totalScore(),
                MatchDecision.CONFLICT,
                result.scores()
        );
    }

    private ReconciliationRecord normalize(ReconciliationRecord record, ReconciliationJob job) {
        ReconciliationRecord normalized = record;

        for (RecordNormalizer normalizer : job.normalizers()) {
            normalized = normalizer.normalize(normalized);
        }

        return normalized;
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

    private record NormalizedRecord(
            ReconciliationRecord original,
            ReconciliationRecord normalized
    ) {
    }
}