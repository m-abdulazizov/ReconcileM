package com.reconcilem.core.rule;

import com.reconcilem.core.model.MatchScore;
import com.reconcilem.core.model.ReconciliationRecord;

public class ReferenceExactMatchRule implements MatchingRule {

    private final int score;

    public ReferenceExactMatchRule() {
        this(25);
    }

    public ReferenceExactMatchRule(int score) {
        this.score = score;
    }

    @Override
    public String name() {
        return "REFERENCE_EXACT_MATCH";
    }

    @Override
    public MatchScore evaluate(ReconciliationRecord source, ReconciliationRecord target) {
        String sourceReference = normalize(source.reference());
        String targetReference = normalize(target.reference());

        boolean matched = !sourceReference.isBlank() && sourceReference.equals(targetReference);

        return new MatchScore(
                name(),
                matched ? score : 0,
                matched ? "References are equal" : "References are different"
        );
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }
}