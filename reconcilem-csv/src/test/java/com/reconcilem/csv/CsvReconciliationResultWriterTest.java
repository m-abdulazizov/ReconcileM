package com.reconcilem.csv;

import com.reconcilem.core.engine.DefaultReconciliationEngine;
import com.reconcilem.core.engine.ReconciliationEngine;
import com.reconcilem.core.model.ReconciliationJob;
import com.reconcilem.core.model.ReconciliationRecord;
import com.reconcilem.core.model.ReconciliationResult;
import com.reconcilem.core.model.ReconciliationThresholds;
import com.reconcilem.core.rule.AmountToleranceRule;
import com.reconcilem.core.rule.CounterpartyContainsRule;
import com.reconcilem.core.rule.CurrencyExactMatchRule;
import com.reconcilem.core.rule.DateToleranceRule;
import com.reconcilem.core.rule.ReferenceContainsRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CsvReconciliationResultWriterTest {

    private final ReconciliationEngine engine = new DefaultReconciliationEngine();
    private final CsvReconciliationResultWriter writer = new CsvReconciliationResultWriter();

    @TempDir
    Path tempDir;

    @Test
    void shouldWriteReconciliationResultFiles() {
        ReconciliationRecord bankTransaction = new ReconciliationRecord(
                "BANK_TX_1",
                "bank",
                LocalDate.of(2026, 6, 1),
                new BigDecimal("999000.00"),
                "UZS",
                "ACME LLC",
                "Payment for invoice INV 889",
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
                        new AmountToleranceRule(new BigDecimal("1000.00"), 40),
                        new CurrencyExactMatchRule(),
                        new DateToleranceRule(),
                        new ReferenceContainsRule(),
                        new CounterpartyContainsRule()
                ),
                new ReconciliationThresholds(80, 50)
        );

        ReconciliationResult result = engine.reconcile(
                List.of(bankTransaction),
                List.of(invoice),
                job
        );

        writer.write(result, tempDir);

        assertThat(tempDir.resolve("matched.csv")).exists();
        assertThat(tempDir.resolve("possible_matches.csv")).exists();
        assertThat(tempDir.resolve("duplicate_matches.csv")).exists();
        assertThat(tempDir.resolve("conflict_matches.csv")).exists();
        assertThat(tempDir.resolve("unmatched_source.csv")).exists();
        assertThat(tempDir.resolve("unmatched_target.csv")).exists();
        assertThat(tempDir.resolve("summary.csv")).exists();

        assertThat(read(tempDir.resolve("matched.csv"))).contains("BANK_TX_1", "INV_889", "MATCHED");
        assertThat(read(tempDir.resolve("summary.csv"))).contains("source_count", "1,1,1,0,0,0");
    }

    private String read(Path path) {
        try {
            return Files.readString(path);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to read file: " + path, ex);
        }
    }
}