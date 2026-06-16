# CSV Module

The CSV module reads financial records from CSV files and exports reconciliation results back to CSV.

Main classes:

```text
CsvMapping
CsvReconciliationRecordReader
CsvReconciliationResultWriter
CsvReadException
CsvWriteException
```

## Reading Records

CSV files from banks, invoice systems, and payment providers rarely use the same column names. `CsvMapping` lets each application define the columns explicitly.

```java
CsvMapping mapping = CsvMapping.builder()
        .sourceName("bank")
        .idColumn("id")
        .transactionDateColumn("date")
        .amountColumn("amount")
        .currencyColumn("currency")
        .counterpartyNameColumn("counterparty")
        .referenceColumn("reference")
        .attributeColumn("bank_account")
        .build();
```

Read records:

```java
List<ReconciliationRecord> records = csvReader.read(inputStream, mapping);
```

## Exporting Results

```java
Path outputDirectory = Path.of("build", "reconciliation-report");
csvWriter.write(result, outputDirectory);
```

Generated files:

```text
matched.csv
possible_matches.csv
duplicate_matches.csv
conflict_matches.csv
unmatched_source.csv
unmatched_target.csv
summary.csv
```

## Error Handling

Read failures are wrapped in `CsvReadException`.

Write failures are wrapped in `CsvWriteException`.

Common causes:

```text
missing required column
invalid date value
invalid amount value
unreadable input stream
unwritable output directory
```

## Recommended CSV Shape

Use stable machine-friendly column names when possible:

```csv
id,date,amount,currency,counterparty,reference
BANK_TX_1001,2026-06-01,999000.00,UZS,ACME LLC,Payment for Invoice INV 889
```

When source files use different names, map them with `CsvMapping` rather than changing framework code.
