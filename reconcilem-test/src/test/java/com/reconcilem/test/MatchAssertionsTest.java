package com.reconcilem.test;

import com.reconcilem.core.model.MatchDecision;
import com.reconcilem.core.model.MatchResult;
import com.reconcilem.core.model.MatchScore;
import com.reconcilem.core.model.ReconciliationRecord;
import com.reconcilem.core.model.ReconciliationResult;
import com.reconcilem.core.model.ReconciliationSummary;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

class MatchAssertionsTest {

    @Test
    void shouldAssertCommonReconciliationResultStates() {
        ReconciliationRecord bankRecord = RecordFixtures.bankRecord(
                "BANK_TX_1",
                new BigDecimal("1000.00"),
                "INV-1"
        );
        ReconciliationRecord invoiceRecord = RecordFixtures.invoiceRecord(
                "INV_1",
                new BigDecimal("1000.00"),
                "INV-1"
        );
        ReconciliationRecord unmatchedSource = RecordFixtures.bankRecord(
                "BANK_TX_2",
                new BigDecimal("500.00"),
                "INV-2"
        );

        ReconciliationResult result = new ReconciliationResult(
                List.of(new MatchResult(
                        bankRecord,
                        invoiceRecord,
                        90,
                        MatchDecision.MATCHED,
                        List.of(new MatchScore("AMOUNT_EXACT_MATCH", 40, "Amounts are equal"))
                )),
                List.of(),
                List.of(),
                List.of(),
                List.of(unmatchedSource),
                List.of(),
                new ReconciliationSummary(2, 1, 1, 0, 1, 0)
        );

        MatchAssertions.assertMatched(result, "BANK_TX_1", "INV_1");
        MatchAssertions.assertUnmatchedSource(result, "BANK_TX_2");
        MatchAssertions.assertSummary(result, 2, 1, 1, 0, 1, 0);
    }
}
