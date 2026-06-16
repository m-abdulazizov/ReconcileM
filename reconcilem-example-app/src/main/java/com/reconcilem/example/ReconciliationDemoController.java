package com.reconcilem.example;

import com.reconcilem.core.engine.ReconciliationEngine;
import com.reconcilem.core.model.ReconciliationJob;
import com.reconcilem.core.model.ReconciliationRecord;
import com.reconcilem.core.model.ReconciliationResult;
import com.reconcilem.csv.CsvMapping;
import com.reconcilem.csv.CsvReconciliationRecordReader;
import com.reconcilem.csv.CsvReconciliationResultWriter;
import com.reconcilem.jdbc.JdbcQueryMapping;
import com.reconcilem.jdbc.JdbcReconciliationRecordReader;
import com.reconcilem.jdbc.JdbcReconciliationResultRepository;
import com.reconcilem.spring.factory.ReconcileMJobFactory;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbcTemplate;
    private final JdbcReconciliationRecordReader jdbcReader;
    private final JdbcReconciliationResultRepository jdbcResultRepository;
    private final ReconcileMJobFactory jobFactory;

    public ReconciliationDemoController(
            ReconciliationEngine reconciliationEngine,
            CsvReconciliationRecordReader csvReader,
            CsvReconciliationResultWriter csvWriter,
            JdbcTemplate jdbcTemplate,
            JdbcReconciliationRecordReader jdbcReader,
            JdbcReconciliationResultRepository jdbcResultRepository,
            ReconcileMJobFactory jobFactory
    ) {
        this.reconciliationEngine = reconciliationEngine;
        this.csvReader = csvReader;
        this.csvWriter = csvWriter;
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcReader = jdbcReader;
        this.jdbcResultRepository = jdbcResultRepository;
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

    @GetMapping("/demo/reconcile-jdbc")
    public ReconciliationResult demoReconcileJdbc() {
        prepareSampleDatabase();

        List<ReconciliationRecord> bankRecords = jdbcReader.read(
                """
                select
                    id,
                    transaction_date,
                    amount,
                    currency,
                    counterparty_name,
                    reference,
                    bank_account
                from demo_bank_transactions
                order by id
                """,
                bankJdbcMapping()
        );

        List<ReconciliationRecord> invoiceRecords = jdbcReader.read(
                """
                select
                    id,
                    transaction_date,
                    amount,
                    currency,
                    counterparty_name,
                    reference,
                    invoice_number
                from demo_invoices
                order by id
                """,
                invoiceJdbcMapping()
        );

        ReconciliationJob job = jobFactory.defaultJob(
                "BANK_DB_TO_INVOICE_DB",
                "bank",
                "invoice-system"
        );

        return reconciliationEngine.reconcile(
                bankRecords,
                invoiceRecords,
                job
        );
    }

    @GetMapping("/demo/reconcile-jdbc/persist")
    public Map<String, Object> demoReconcileJdbcPersist() {
        ReconciliationResult result = demoReconcileJdbc();

        jdbcResultRepository.createSchema();
        String runId = jdbcResultRepository.save("BANK_DB_TO_INVOICE_DB", result);

        Integer savedMatches = jdbcTemplate.queryForObject(
                "select count(*) from reconcilem_match_result where run_id = ?",
                Integer.class,
                runId
        );
        Integer savedUnmatched = jdbcTemplate.queryForObject(
                "select count(*) from reconcilem_unmatched_record where run_id = ?",
                Integer.class,
                runId
        );

        return Map.of(
                "message", "JDBC reconciliation result persisted",
                "runId", runId,
                "savedMatches", savedMatches,
                "savedUnmatched", savedUnmatched,
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

    private JdbcQueryMapping bankJdbcMapping() {
        return JdbcQueryMapping.builder()
                .sourceName("bank")
                .idColumn("id")
                .transactionDateColumn("transaction_date")
                .amountColumn("amount")
                .currencyColumn("currency")
                .counterpartyNameColumn("counterparty_name")
                .referenceColumn("reference")
                .attributeColumn("bank_account")
                .build();
    }

    private JdbcQueryMapping invoiceJdbcMapping() {
        return JdbcQueryMapping.builder()
                .sourceName("invoice-system")
                .idColumn("id")
                .transactionDateColumn("transaction_date")
                .amountColumn("amount")
                .currencyColumn("currency")
                .counterpartyNameColumn("counterparty_name")
                .referenceColumn("reference")
                .attributeColumn("invoice_number")
                .build();
    }

    private void prepareSampleDatabase() {
        jdbcTemplate.execute("drop table if exists demo_bank_transactions");
        jdbcTemplate.execute("drop table if exists demo_invoices");

        jdbcTemplate.execute("""
                create table demo_bank_transactions (
                    id varchar(100) primary key,
                    transaction_date date not null,
                    amount numeric(19, 2) not null,
                    currency varchar(10),
                    counterparty_name varchar(255),
                    reference varchar(255),
                    bank_account varchar(100)
                )
                """);

        jdbcTemplate.execute("""
                create table demo_invoices (
                    id varchar(100) primary key,
                    transaction_date date not null,
                    amount numeric(19, 2) not null,
                    currency varchar(10),
                    counterparty_name varchar(255),
                    reference varchar(255),
                    invoice_number varchar(100)
                )
                """);

        jdbcTemplate.update("""
                insert into demo_bank_transactions (
                    id,
                    transaction_date,
                    amount,
                    currency,
                    counterparty_name,
                    reference,
                    bank_account
                ) values (?, ?, ?, ?, ?, ?, ?)
                """,
                "BANK_TX_1001",
                "2026-06-01",
                "999000.00",
                "UZS",
                "ACME LLC",
                "Payment for Invoice INV 889",
                "UZ123"
        );

        jdbcTemplate.update("""
                insert into demo_bank_transactions (
                    id,
                    transaction_date,
                    amount,
                    currency,
                    counterparty_name,
                    reference,
                    bank_account
                ) values (?, ?, ?, ?, ?, ?, ?)
                """,
                "BANK_TX_1002",
                "2026-06-03",
                "250000.00",
                "UZS",
                "BRAVO LLC",
                "Payment without matching invoice",
                "UZ123"
        );

        jdbcTemplate.update("""
                insert into demo_invoices (
                    id,
                    transaction_date,
                    amount,
                    currency,
                    counterparty_name,
                    reference,
                    invoice_number
                ) values (?, ?, ?, ?, ?, ?, ?)
                """,
                "INV_889",
                "2026-05-30",
                "1000000.00",
                "UZS",
                "ACME LIMITED",
                "INV-889",
                "INV-889"
        );
    }
}
