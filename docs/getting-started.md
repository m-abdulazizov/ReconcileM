# Getting Started with ReconcileM

ReconcileM is a Java financial reconciliation framework. It helps developers match financial records from different systems such as banks, invoices, payment gateways, refunds, accounting exports, and internal databases.

This guide shows how to use ReconcileM in a Spring Boot application.

---

## 1. Add dependency

When ReconcileM is published, users will be able to add it like this:

```kotlin
implementation("io.github.mabdulazizov:reconcilem-spring-boot-starter:0.1.0")
```

During local development inside this repository, the example app uses:

```kotlin
implementation(project(":reconcilem-spring-boot-starter"))
```

---

## 2. Configure ReconcileM

Add configuration to `application.yml`:

```yaml
reconcilem:
  thresholds:
    matched-score: 80
    possible-match-score: 50
  rules:
    amount-tolerance: 1000.00
    amount-score: 40
    currency-score: 10
    date-tolerance-days: 3
    date-score: 15
    reference-score: 20
    counterparty-score: 10
```

Explanation:

```text
matched-score: score required to mark records as MATCHED
possible-match-score: score required to mark records as POSSIBLE_MATCH
amount-tolerance: allowed difference between two amounts
amount-score: score given when amount rule matches
currency-score: score given when currencies match
date-tolerance-days: allowed date difference
date-score: score given when dates are within tolerance
reference-score: score given when references match
counterparty-score: score given when counterparty names match
```

---

## 3. Inject ReconcileM beans

The Spring Boot starter automatically creates these beans:

```java
ReconciliationEngine
CsvReconciliationRecordReader
CsvReconciliationResultWriter
ReconcileMJobFactory
```

Example:

```java
@Service
public class BankReconciliationService {

    private final ReconciliationEngine reconciliationEngine;
    private final CsvReconciliationRecordReader csvReader;
    private final ReconcileMJobFactory jobFactory;

    public BankReconciliationService(
            ReconciliationEngine reconciliationEngine,
            CsvReconciliationRecordReader csvReader,
            ReconcileMJobFactory jobFactory
    ) {
        this.reconciliationEngine = reconciliationEngine;
        this.csvReader = csvReader;
        this.jobFactory = jobFactory;
    }
}
```

---

## 4. Read CSV records

Example CSV:

```csv
id,date,amount,currency,counterparty,reference
BANK_TX_1001,2026-06-01,999000.00,UZS,ACME LLC,Payment for Invoice INV 889
```

Define mapping:

```java
CsvMapping bankMapping = CsvMapping.builder()
        .sourceName("bank")
        .idColumn("id")
        .transactionDateColumn("date")
        .amountColumn("amount")
        .currencyColumn("currency")
        .counterpartyNameColumn("counterparty")
        .referenceColumn("reference")
        .build();
```

Read records:

```java
List<ReconciliationRecord> bankRecords = csvReader.read(inputStream, bankMapping);
```

---

## 5. Create reconciliation job

Use the default job factory:

```java
ReconciliationJob job = jobFactory.defaultJob(
        "BANK_TO_INVOICE",
        "bank",
        "invoice-system"
);
```

The default job uses these rules:

```text
AmountToleranceRule
CurrencyExactMatchRule
DateToleranceRule
ReferenceContainsRule
CounterpartyContainsRule
```

---

## 6. Run reconciliation

```java
ReconciliationResult result = reconciliationEngine.reconcile(
        bankRecords,
        invoiceRecords,
        job
);
```

Result contains:

```text
matched
possibleMatches
duplicateMatches
conflictMatches
unmatchedSourceRecords
unmatchedTargetRecords
summary
```

---

## 7. Export result to CSV

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

---

## 8. Run the example app

From project root:

```powershell
.\gradlew.bat :reconcilem-example-app:bootRun
```

Open:

```text
http://localhost:8081/demo/reconcile-csv
```

Export report:

```text
http://localhost:8081/demo/reconcile-csv/export
```

---

## 9. Example output summary

```json
{
  "sourceCount": 3,
  "targetCount": 2,
  "matchedCount": 1,
  "possibleMatchCount": 0,
  "unmatchedSourceCount": 2,
  "unmatchedTargetCount": 1
}
```

---

## 10. JDBC Support

ReconcileM can also read records directly from database queries using the `reconcilem-jdbc` module.

See [JDBC Support](jdbc-support.md).

This means:

```text
3 source records were loaded
2 target records were loaded
1 source record matched a target record
2 source records stayed unmatched
1 target record stayed unmatched
```
