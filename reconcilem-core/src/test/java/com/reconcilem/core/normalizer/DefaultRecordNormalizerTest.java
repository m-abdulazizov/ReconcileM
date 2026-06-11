package com.reconcilem.core.normalizer;

import com.reconcilem.core.model.ReconciliationRecord;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultRecordNormalizerTest
{
    private final RecordNormalizer normalizer = new DefaultRecordNormalizer();

    @Test
    void shouldNormalizeCurrencyReferenceAndCounterpartyName() {
        ReconciliationRecord record = new ReconciliationRecord(
                "1",
                "bank",
                LocalDate.of(2026, 6, 1),
                new BigDecimal("1000.00"),
                " uzs ",
                "  Acme L.L.C.  ",
                " inv-889 ",
                Map.of()
        );

        ReconciliationRecord normalized = normalizer.normalize(record);

        assertThat(normalized.currency()).isEqualTo("UZS");
        assertThat(normalized.reference()).isEqualTo("INV889");
        assertThat(normalized.counterpartyName()).isEqualTo("ACME");
    }
}
