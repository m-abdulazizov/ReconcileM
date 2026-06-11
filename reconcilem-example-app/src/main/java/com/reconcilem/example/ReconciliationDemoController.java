package com.reconcilem.example;
import com.reconcilem.core.engine.DefaultReconciliationEngine;
import com.reconcilem.core.engine.ReconciliationEngine;
import com.reconcilem.core.model.ReconciliationJob;
import com.reconcilem.core.model.ReconciliationRecord;
import com.reconcilem.core.model.ReconciliationResult;
import com.reconcilem.core.model.ReconciliationThresholds;
import com.reconcilem.core.rule.*;
import com.reconcilem.csv.CsvMapping;
import com.reconcilem.csv.CsvReconciliationRecordReader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

@RestController
public class ReconciliationDemoController
{
    private final ReconciliationEngine reconciliationEngine = new DefaultReconciliationEngine();

    @GetMapping("/demo/reconcile-csv")
    public ReconciliationResult demoReconcileCsv() {
        CsvReconciliationRecordReader reader = new CsvReconciliationRecordReader();

        CsvMapping bankMapping = CsvMapping.builder()
                .sourceName("bank")
                .idColumn("id")
                .transactionDateColumn("date")
                .amountColumn("amount")
                .currencyColumn("currency")
                .counterpartyNameColumn("counterparty")
                .referenceColumn("reference")
                .build();

        CsvMapping invoiceMapping = CsvMapping.builder()
                .sourceName("invoice-system")
                .idColumn("id")
                .transactionDateColumn("date")
                .amountColumn("amount")
                .currencyColumn("currency")
                .counterpartyNameColumn("counterparty")
                .referenceColumn("reference")
                .build();

        try (
                InputStream bankStream = openResource("/sample-bank.csv");
                InputStream invoiceStream = openResource("/sample-invoices.csv")
        ) {
            List<ReconciliationRecord> bankRecords = reader.read(bankStream, bankMapping);
            List<ReconciliationRecord> invoiceRecords = reader.read(invoiceStream, invoiceMapping);

            ReconciliationJob job = new ReconciliationJob(
                    "BANK_CSV_TO_INVOICE_CSV",
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

            return reconciliationEngine.reconcile(
                    bankRecords,
                    invoiceRecords,
                    job
            );
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read sample CSV resources", ex);
        }

    }
    private InputStream openResource(String resourcePath) {
        InputStream inputStream = getClass().getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IllegalStateException("Resource not found: " + resourcePath);
        }

        return inputStream;
    }

}