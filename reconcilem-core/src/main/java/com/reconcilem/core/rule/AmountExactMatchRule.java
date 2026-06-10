package com.reconcilem.core.rule;

import com.reconcilem.core.model.MatchScore;
import com.reconcilem.core.model.ReconciliationRecord;

public class AmountExactMatchRule implements MatchingRule {

    private final int score;

    public AmountExactMatchRule() {
        this(40);
    }

    public AmountExactMatchRule(int score) {
        this.score = score;
    }

    @Override
    public String name() {
        return "AMOUNT_EXACT_MATCH";
    }

    @Override
    public MatchScore evaluate(ReconciliationRecord source, ReconciliationRecord target) {
        boolean matched = source.amount().compareTo(target.amount()) == 0;

        return new MatchScore(
                name(),
                matched ? score : 0,
                matched ? "Amounts are equal" : "Amounts are different"
        );
    }
}