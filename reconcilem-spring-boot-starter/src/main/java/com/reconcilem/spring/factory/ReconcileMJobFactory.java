package com.reconcilem.spring.factory;

import com.reconcilem.core.model.ReconciliationJob;
import com.reconcilem.core.model.ReconciliationThresholds;
import com.reconcilem.core.rule.AmountToleranceRule;
import com.reconcilem.core.rule.CounterpartyContainsRule;
import com.reconcilem.core.rule.CurrencyExactMatchRule;
import com.reconcilem.core.rule.DateToleranceRule;
import com.reconcilem.core.rule.MatchingRule;
import com.reconcilem.core.rule.ReferenceContainsRule;
import com.reconcilem.spring.properties.ReconcileMProperties;

import java.util.List;
import java.util.Objects;

public class ReconcileMJobFactory {

    private final ReconcileMProperties properties;

    public ReconcileMJobFactory(ReconcileMProperties properties) {
        this.properties = Objects.requireNonNull(properties, "ReconcileM properties must not be null");
    }

    public ReconciliationJob defaultJob(String name, String sourceName, String targetName) {
        return new ReconciliationJob(
                name,
                sourceName,
                targetName,
                defaultRules(),
                defaultThresholds()
        );
    }

    public ReconciliationThresholds defaultThresholds() {
        return new ReconciliationThresholds(
                properties.getThresholds().getMatchedScore(),
                properties.getThresholds().getPossibleMatchScore()
        );
    }

    public List<MatchingRule> defaultRules() {
        ReconcileMProperties.Rules rules = properties.getRules();

        return List.of(
                new AmountToleranceRule(rules.getAmountTolerance(), rules.getAmountScore()),
                new CurrencyExactMatchRule(rules.getCurrencyScore()),
                new DateToleranceRule(rules.getDateToleranceDays(), rules.getDateScore()),
                new ReferenceContainsRule(rules.getReferenceScore()),
                new CounterpartyContainsRule(rules.getCounterpartyScore())
        );
    }
}