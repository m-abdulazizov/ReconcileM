package com.reconcilem.core.rule;

import com.reconcilem.core.model.MatchScore;
import com.reconcilem.core.model.ReconciliationRecord;

public class CounterpartyContainsRule implements MatchingRule {

    private final int score;

    public CounterpartyContainsRule() {
        this(10);
    }

    public CounterpartyContainsRule(int score) {
        this.score = score;
    }

    @Override
    public String name() {
        return "COUNTERPARTY_CONTAINS_MATCH";
    }

    @Override
    public MatchScore evaluate(ReconciliationRecord source, ReconciliationRecord target) {
        String sourceName = normalize(source.counterpartyName());
        String targetName = normalize(target.counterpartyName());

        boolean matched = !sourceName.isBlank()
                && !targetName.isBlank()
                && (sourceName.contains(targetName) || targetName.contains(sourceName));

        return new MatchScore(
                name(),
                matched ? score : 0,
                matched ? "Counterparty names are similar" : "Counterparty names are different"
        );
    }

    private String normalize(String value) {
        return value == null
                ? ""
                : value.trim()
                .toUpperCase()
                .replace(".", "")
                .replace(",", "")
                .replace(" LLC", "")
                .replace(" LTD", "")
                .replace(" LIMITED", "");
    }
}