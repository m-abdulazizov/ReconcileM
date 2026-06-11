package com.reconcilem.core.rule;

import com.reconcilem.core.model.MatchScore;
import com.reconcilem.core.model.ReconciliationRecord;

public class ReferenceContainsRule implements MatchingRule
{
    private final int score;

    public ReferenceContainsRule() {
        this(20);
    }

    public ReferenceContainsRule(int score) {
        if (score < 0) {
            throw new IllegalArgumentException("Score must not be negative");
        }

        this.score = score;
    }

    @Override
    public String name() {
        return "REFERENCE_CONTAINS_MATCH";
    }

    @Override
    public MatchScore evaluate(ReconciliationRecord source, ReconciliationRecord target) {
        String sourceReference = normalize(source.reference());
        String targetReference = normalize(target.reference());

        boolean matched = !sourceReference.isBlank()
                && !targetReference.isBlank()
                && (
                sourceReference.contains(targetReference)
                        || targetReference.contains(sourceReference)
        );

        return new MatchScore(
                name(),
                matched ? score : 0,
                matched ? "One reference contains the other reference" : "References do not contain each other"
        );
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value.trim()
                .toUpperCase()
                .replace("№", "NO")
                .replaceAll("[^A-Z0-9]", "");
    }
}