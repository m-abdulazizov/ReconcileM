package com.reconcilem.example;

import com.reconcilem.core.engine.ReconciliationEngine;
import com.reconcilem.core.model.ReconciliationJob;
import com.reconcilem.core.model.ReconciliationRecord;
import com.reconcilem.core.model.ReconciliationResult;
import com.reconcilem.csv.CsvMapping;
import com.reconcilem.csv.CsvReconciliationRecordReader;
import com.reconcilem.csv.CsvReconciliationResultWriter;
import com.reconcilem.spring.factory.ReconcileMJobFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
public class ReconciliationDemoController {

    private final ReconciliationEngine reconciliationEngine;
    private final CsvReconciliationRecordReader csvReader;
    private final CsvReconciliationResultWriter csvWriter;
    private final ReconcileMJobFactory jobFactory;

    public ReconciliationDemoController(
            ReconciliationEngine reconciliationEngine,
            CsvReconciliationRecordReader csvReader,
            CsvReconciliationResultWriter csvWriter,
            ReconcileMJobFactory jobFactory
    ) {
        this.reconciliationEngine = reconciliationEngine;
        this.csvReader = csvReader;
        this.csvWriter = csvWriter;
        this.jobFactory = jobFactory;
    }

    @GetMapping("/demo/reconcile-csv")
    public ReconciliationResult demoReconcileCsv() {
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
            List<ReconciliationRecord> bankRecords = csvReader.read(bankStream, bankMapping);
            List<ReconciliationRecord> invoiceRecords = csvReader.read(invoiceStream, invoiceMapping);

            ReconciliationJob job = jobFactory.defaultJob(
                    "BANK_CSV_TO_INVOICE_CSV",
                    "bank",
                    "invoice-system"
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

    @GetMapping("/demo/reconcile-csv/export")
    public Map<String, Object> demoReconcileCsvExport() {
        ReconciliationResult result = demoReconcileCsv();

        Path outputDirectory = Path.of("build", "reconcilem-demo-report");

        csvWriter.write(result, outputDirectory);

        return Map.of(
                "message", "CSV reconciliation report generated",
                "outputDirectory", outputDirectory.toAbsolutePath().toString(),
                "files", List.of(
                        "matched.csv",
                        "possible_matches.csv",
                        "duplicate_matches.csv",
                        "conflict_matches.csv",
                        "unmatched_source.csv",
                        "unmatched_target.csv",
                        "summary.csv"
                ),
                "summary", result.summary()
        );
    }

    private InputStream openResource(String resourcePath) {
        InputStream inputStream = getClass().getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IllegalStateException("Resource not found: " + resourcePath);
        }

        return inputStream;
    }
}