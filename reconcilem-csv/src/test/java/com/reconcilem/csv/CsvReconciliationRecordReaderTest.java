package com.reconcilem.csv;

import com.reconcilem.core.model.ReconciliationRecord;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CsvReconciliationRecordReaderTest {

    private final CsvReconciliationRecordReader reader = new CsvReconciliationRecordReader();

    @Test
    void shouldReadReconciliationRecordsFromCsv() {
        String csv = """
                id,date,amount,currency,counterparty,reference
                BANK_TX_1,2026-06-01,1000.00,UZS,ACME LLC,INV-1
                """;

        CsvMapping mapping = CsvMapping.builder()
                .sourceName("bank")
                .idColumn("id")
                .transactionDateColumn("date")
                .amountColumn("amount")
                .currencyColumn("currency")
                .counterpartyNameColumn("counterparty")
                .referenceColumn("reference")
                .build();

        List<ReconciliationRecord> records = reader.read(
                new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8)),
                mapping
        );

        assertThat(records).hasSize(1);

        ReconciliationRecord record = records.getFirst();

        assertThat(record.id()).isEqualTo("BANK_TX_1");
        assertThat(record.source()).isEqualTo("bank");
        assertThat(record.amount()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(record.currency()).isEqualTo("UZS");
        assertThat(record.counterpartyName()).isEqualTo("ACME LLC");
        assertThat(record.reference()).isEqualTo("INV-1");
    }
}