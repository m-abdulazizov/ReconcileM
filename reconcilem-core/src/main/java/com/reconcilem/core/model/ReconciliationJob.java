package com.reconcilem.core.model;

import com.reconcilem.core.rule.MatchingRule;

import java.util.List;
import java.util.Objects;

public record ReconciliationJob(
        String name,
        String sourceName,
        String targetName,
        List<MatchingRule> rules,
        ReconciliationThresholds thresholds
) {

    public ReconciliationJob {
        Objects.requireNonNull(name, "Job name must not be null");
        Objects.requireNonNull(sourceName, "Source name must not be null");
        Objects.requireNonNull(targetName, "Target name must not be null");

        rules = rules == null ? List.of() : List.copyOf(rules);
        thresholds = thresholds == null ? ReconciliationThresholds.defaults() : thresholds;

        if (rules.isEmpty()) {
            throw new IllegalArgumentException("Reconciliation job must contain at least one matching rule");
        }
    }
}