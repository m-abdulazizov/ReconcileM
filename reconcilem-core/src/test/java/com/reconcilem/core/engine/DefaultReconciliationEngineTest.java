package com.reconcilem.core.engine;

import com.reconcilem.core.model.MatchDecision;
import com.reconcilem.core.model.ReconciliationJob;
import com.reconcilem.core.model.ReconciliationRecord;
import com.reconcilem.core.model.ReconciliationResult;
import com.reconcilem.core.model.ReconciliationThresholds;
import com.reconcilem.core.rule.AmountExactMatchRule;
import com.reconcilem.core.rule.CounterpartyContainsRule;
import com.reconcilem.core.rule.CurrencyExactMatchRule;
import com.reconcilem.core.rule.DateToleranceRule;
import com.reconcilem.core.rule.ReferenceExactMatchRule;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultReconciliationEngineTest {

    private final ReconciliationEngine engine = new DefaultReconciliationEngine();

    @Test
    void shouldMatchBankTransactionWithInvoice() {
        ReconciliationRecord bankTransaction = new ReconciliationRecord(
                "BANK_TX_1001",
                "bank",
                LocalDate.of(2026, 6, 1),
                new BigDecimal("1000000.00"),
                "UZS",
                "ACME LLC",
                "INV-889",
                Map.of()
        );

        ReconciliationRecord invoice = new ReconciliationRecord(
                "INV_889",
                "invoice-system",
                LocalDate.of(2026, 5, 30),
                new BigDecimal("1000000.00"),
                "UZS",
                "ACME Limited",
                "INV-889",
                Map.of()
        );

        ReconciliationJob job = new ReconciliationJob(
                "BANK_TO_INVOICE",
                "bank",
                "invoice-system",
                List.of(
                        new AmountExactMatchRule(),
                        new CurrencyExactMatchRule(),
                        new DateToleranceRule(),
                        new ReferenceExactMatchRule(),
                        new CounterpartyContainsRule()
                ),
                new ReconciliationThresholds(80, 50)
        );

        ReconciliationResult result = engine.reconcile(
                List.of(bankTransaction),
                List.of(invoice),
                job
        );

        assertThat(result.matched()).hasSize(1);
        assertThat(result.possibleMatches()).isEmpty();
        assertThat(result.unmatchedSourceRecords()).isEmpty();
        assertThat(result.unmatchedTargetRecords()).isEmpty();

        assertThat(result.matched().getFirst().decision()).isEqualTo(MatchDecision.MATCHED);
        assertThat(result.matched().getFirst().totalScore()).isGreaterThanOrEqualTo(80);
    }
}