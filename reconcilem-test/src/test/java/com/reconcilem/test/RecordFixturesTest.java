package com.reconcilem.test;

import com.reconcilem.core.model.ReconciliationRecord;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class RecordFixturesTest {

    @Test
    void shouldCreateCustomRecordFixture() {
        ReconciliationRecord record = RecordFixtures.builder()
                .id("BANK_TX_42")
                .source("bank")
                .transactionDate(LocalDate.of(2026, 6, 16))
                .amount(new BigDecimal("125000.00"))
                .currency("uzs")
                .counterpartyName("ACME LLC")
                .reference("INV-42")
                .attribute("bank_account", "UZ123")
                .build();

        assertThat(record.id()).isEqualTo("BANK_TX_42");
        assertThat(record.source()).isEqualTo("bank");
        assertThat(record.transactionDate()).isEqualTo(LocalDate.of(2026, 6, 16));
        assertThat(record.amount()).isEqualByComparingTo("125000.00");
        assertThat(record.currency()).isEqualTo("UZS");
        assertThat(record.attributes()).containsEntry("bank_account", "UZ123");
    }
}
