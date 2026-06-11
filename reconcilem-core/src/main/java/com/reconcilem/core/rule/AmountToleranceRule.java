package com.reconcilem.core.rule;

import com.reconcilem.core.model.MatchScore;
import com.reconcilem.core.model.ReconciliationRecord;

import java.math.BigDecimal;

public class AmountToleranceRule implements MatchingRule
{
    private final BigDecimal toleranceAmount;
    private final int score;

    public AmountToleranceRule() {
        this(new BigDecimal("0.00"), 40);
    }

    public AmountToleranceRule(BigDecimal toleranceAmount, int score) {
        if (toleranceAmount == null || toleranceAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Tolerance amount must not be null or negative");
        }

        if (score < 0) {
            throw new IllegalArgumentException("Score must not be negative");
        }

        this.toleranceAmount = toleranceAmount;
        this.score = score;
    }

    @Override
    public String name() {
        return "AMOUNT_TOLERANCE_MATCH";
    }

    @Override
    public MatchScore evaluate(ReconciliationRecord source, ReconciliationRecord target) {
        BigDecimal difference = source.amount()
                .subtract(target.amount())
                .abs();

        boolean matched = difference.compareTo(toleranceAmount) <= 0;

        return new MatchScore(
                name(),
                matched ? score : 0,
                matched
                        ? "Amount difference is within tolerance: " + difference
                        : "Amount difference is outside tolerance: " + difference
        );
    }
}
