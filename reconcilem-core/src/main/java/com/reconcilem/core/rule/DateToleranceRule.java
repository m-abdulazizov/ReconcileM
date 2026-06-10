package com.reconcilem.core.rule;

import com.reconcilem.core.model.MatchScore;
import com.reconcilem.core.model.ReconciliationRecord;

import java.time.temporal.ChronoUnit;

public class DateToleranceRule implements MatchingRule {

    private final int toleranceDays;
    private final int score;

    public DateToleranceRule() {
        this(3, 15);
    }

    public DateToleranceRule(int toleranceDays, int score) {
        if (toleranceDays < 0) {
            throw new IllegalArgumentException("Tolerance days must not be negative");
        }

        this.toleranceDays = toleranceDays;
        this.score = score;
    }

    @Override
    public String name() {
        return "DATE_TOLERANCE_MATCH";
    }

    @Override
    public MatchScore evaluate(ReconciliationRecord source, ReconciliationRecord target) {
        if (source.transactionDate() == null || target.transactionDate() == null) {
            return new MatchScore(name(), 0, "One or both dates are missing");
        }

        long difference = Math.abs(ChronoUnit.DAYS.between(source.transactionDate(), target.transactionDate()));
        boolean matched = difference <= toleranceDays;

        return new MatchScore(
                name(),
                matched ? score : 0,
                matched
                        ? "Dates are within tolerance: " + difference + " day(s)"
                        : "Dates are outside tolerance: " + difference + " day(s)"
        );
    }
}