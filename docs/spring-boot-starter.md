# Spring Boot Starter

The Spring Boot starter provides default ReconcileM beans for Spring Boot applications.

Dependency after publishing:

```kotlin
implementation("io.github.mabdulazizov:reconcilem-spring-boot-starter:0.1.0")
```

Local development dependency:

```kotlin
implementation(project(":reconcilem-spring-boot-starter"))
```

## Auto-Configured Beans

The starter creates these beans by default:

```text
ReconciliationEngine
CsvReconciliationRecordReader
CsvReconciliationResultWriter
ReconcileMJobFactory
```

When a `DataSource` bean exists, it also creates:

```text
JdbcReconciliationRecordReader
JdbcReconciliationResultRepository
```

## Configuration

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

## Usage

```java
@Service
public class BankReconciliationService {

    private final ReconciliationEngine reconciliationEngine;
    private final ReconcileMJobFactory jobFactory;

    public BankReconciliationService(
            ReconciliationEngine reconciliationEngine,
            ReconcileMJobFactory jobFactory
    ) {
        this.reconciliationEngine = reconciliationEngine;
        this.jobFactory = jobFactory;
    }

    public ReconciliationResult reconcile(
            List<ReconciliationRecord> bankRecords,
            List<ReconciliationRecord> invoiceRecords
    ) {
        ReconciliationJob job = jobFactory.defaultJob(
                "BANK_TO_INVOICE",
                "bank",
                "invoice-system"
        );

        return reconciliationEngine.reconcile(bankRecords, invoiceRecords, job);
    }
}
```

## Bean Override

Applications can replace default framework beans by defining their own bean of the same type.

```java
@Bean
public ReconciliationEngine reconciliationEngine() {
    return new DefaultReconciliationEngine();
}
```

The starter uses conditional bean registration, so custom application beans win.
