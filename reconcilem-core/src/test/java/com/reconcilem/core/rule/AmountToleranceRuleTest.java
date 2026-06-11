package com.reconcilem.core.rule;

import com.reconcilem.core.model.MatchScore;
import com.reconcilem.core.model.ReconciliationRecord;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AmountToleranceRuleTest {

    @Test
    void shouldMatchWhenAmountDifferenceIsWithinTolerance() {
        ReconciliationRecord source = record("SRC-1", "1000000.00");
        ReconciliationRecord target = record("TRG-1", "999000.00");

        AmountToleranceRule rule = new AmountToleranceRule(new BigDecimal("1000.00"), 40);

        MatchScore score = rule.evaluate(source, target);

        assertThat(score.score()).isEqualTo(40);
    }

    @Test
    void shouldNotMatchWhenAmountDifferenceIsOutsideTolerance() {
        ReconciliationRecord source = record("SRC-1", "1000000.00");
        ReconciliationRecord target = record("TRG-1", "998000.00");

        AmountToleranceRule rule = new AmountToleranceRule(new BigDecimal("1000.00"), 40);

        MatchScore score = rule.evaluate(source, target);

        assertThat(score.score()).isZero();
    }

    private ReconciliationRecord record(String id, String amount) {
        return new ReconciliationRecord(
                id,
                "test",
                LocalDate.of(2026, 6, 1),
                new BigDecimal(amount),
                "UZS",
                "ACME",
                "INV-889",
                Map.of()
        );
    }
}