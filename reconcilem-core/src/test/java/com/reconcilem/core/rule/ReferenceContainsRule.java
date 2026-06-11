package com.reconcilem.core.rule;

import com.reconcilem.core.model.MatchScore;
import com.reconcilem.core.model.ReconciliationRecord;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ReferenceContainsRuleTest {

    @Test
    void shouldMatchWhenReferenceIsInsideLongDescription() {
        ReconciliationRecord source = record("SRC-1", "Payment for invoice INV 889 from client");
        ReconciliationRecord target = record("TRG-1", "INV-889");

        ReferenceContainsRule rule = new ReferenceContainsRule(20);

        MatchScore score = rule.evaluate(source, target);

        assertThat(score.score()).isEqualTo(20);
    }

    @Test
    void shouldNotMatchWhenReferencesAreDifferent() {
        ReconciliationRecord source = record("SRC-1", "Payment for invoice INV 777");
        ReconciliationRecord target = record("TRG-1", "INV-889");

        ReferenceContainsRule rule = new ReferenceContainsRule(20);

        MatchScore score = rule.evaluate(source, target);

        assertThat(score.score()).isZero();
    }

    private ReconciliationRecord record(String id, String reference) {
        return new ReconciliationRecord(
                id,
                "test",
                LocalDate.of(2026, 6, 1),
                new BigDecimal("1000000.00"),
                "UZS",
                "ACME",
                reference,
                Map.of()
        );
    }
}