package com.reconcilem.core.rule;

import com.reconcilem.core.model.MatchScore;
import com.reconcilem.core.model.ReconciliationRecord;

public class CurrencyExactMatchRule implements MatchingRule {

    private final int score;

    public CurrencyExactMatchRule() {
        this(10);
    }

    public CurrencyExactMatchRule(int score) {
        this.score = score;
    }

    @Override
    public String name() {
        return "CURRENCY_EXACT_MATCH";
    }

    @Override
    public MatchScore evaluate(ReconciliationRecord source, ReconciliationRecord target) {
        boolean matched = source.currency().equalsIgnoreCase(target.currency());

        return new MatchScore(
                name(),
                matched ? score : 0,
                matched ? "Currencies are equal" : "Currencies are different"
        );
    }
}