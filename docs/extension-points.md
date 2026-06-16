# Extension Points

ReconcileM is designed as a framework. Applications are expected to reuse the engine and plug in their own domain-specific behavior.

## Custom Matching Rules

Create a class that implements `MatchingRule`.

```java
public class TaxIdMatchRule implements MatchingRule {

    @Override
    public MatchScore evaluate(ReconciliationRecord source, ReconciliationRecord target) {
        Object sourceTaxId = source.attributes().get("tax_id");
        Object targetTaxId = target.attributes().get("tax_id");

        if (sourceTaxId != null && sourceTaxId.equals(targetTaxId)) {
            return new MatchScore("TAX_ID_MATCH", 15, "Tax IDs are equal");
        }

        return new MatchScore("TAX_ID_MATCH", 0, "Tax IDs are not equal");
    }
}
```

## Custom Normalizers

Create a class that implements `RecordNormalizer`.

```java
public class UzbekCompanyNameNormalizer implements RecordNormalizer {

    @Override
    public ReconciliationRecord normalize(ReconciliationRecord record) {
        String normalizedName = record.counterpartyName()
                .replace("MCHJ", "")
                .replace("OOO", "")
                .trim();

        return new ReconciliationRecord(
                record.id(),
                record.source(),
                record.transactionDate(),
                record.amount(),
                record.currency(),
                normalizedName,
                record.reference(),
                record.attributes()
        );
    }
}
```

## Custom Readers

The core module does not require CSV or JDBC. Applications can create records from any source:

```text
REST APIs
message queues
Excel files
SFTP files
third-party SDKs
internal services
```

The only requirement is to produce `List<ReconciliationRecord>`.

## Custom Exporters

Applications can export `ReconciliationResult` to:

```text
CSV
database tables
audit logs
REST responses
manual review queues
business dashboards
```

## Testing Utilities

The `reconcilem-test` module provides helpers for framework users:

```java
ReconciliationRecord bankRecord = RecordFixtures.bankRecord(
        "BANK_TX_1",
        new BigDecimal("1000.00"),
        "INV-1"
);

MatchAssertions.assertMatched(result, "BANK_TX_1", "INV_1");
```

These helpers keep application tests focused on reconciliation behavior instead of repetitive setup.
