package com.reconcilem.core.model;

import com.reconcilem.core.normalizer.DefaultRecordNormalizer;
import com.reconcilem.core.normalizer.RecordNormalizer;
import com.reconcilem.core.rule.MatchingRule;

import java.util.List;
import java.util.Objects;

public record ReconciliationJob(
        String name,
        String sourceName,
        String targetName,
        List<MatchingRule> rules,
        ReconciliationThresholds thresholds,
        List<RecordNormalizer> normalizers
) {

    public ReconciliationJob(
            String name,
            String sourceName,
            String targetName,
            List<MatchingRule> rules,
            ReconciliationThresholds thresholds
    ) {
        this(
                name,
                sourceName,
                targetName,
                rules,
                thresholds,
                List.of(new DefaultRecordNormalizer())
        );
    }

    public ReconciliationJob {
        Objects.requireNonNull(name, "Job name must not be null");
        Objects.requireNonNull(sourceName, "Source name must not be null");
        Objects.requireNonNull(targetName, "Target name must not be null");

        rules = rules == null ? List.of() : List.copyOf(rules);
        thresholds = thresholds == null ? ReconciliationThresholds.defaults() : thresholds;
        normalizers = normalizers == null ? List.of(new DefaultRecordNormalizer()) : List.copyOf(normalizers);

        if (rules.isEmpty()) {
            throw new IllegalArgumentException("Reconciliation job must contain at least one matching rule");
        }
    }
}