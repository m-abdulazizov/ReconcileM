# JDBC Support

ReconcileM JDBC support allows developers to read reconciliation records directly from database queries.

This is useful when financial data already exists in tables such as:

```text
bank_transactions
invoices
payments
refunds
gateway_settlements
accounting_entries
```

The JDBC module converts SQL query results into `ReconciliationRecord` objects.

---

## 1. Module

JDBC support is located in:

```text
reconcilem-jdbc
```

Main classes:

```text
JdbcReconciliationRecordReader
JdbcQueryMapping
JdbcReadException
```

---

## 2. Add dependency

When ReconcileM is published:

```kotlin
implementation("io.github.mabdulazizov:reconcilem-jdbc:0.1.0")
```

For Spring Boot users, the starter already includes JDBC support:

```kotlin
implementation("io.github.mabdulazizov:reconcilem-spring-boot-starter:0.1.0")
```

---

## 3. Basic usage

Create a mapping:

```java
JdbcQueryMapping mapping = JdbcQueryMapping.builder()
        .sourceName("bank")
        .idColumn("id")
        .transactionDateColumn("transaction_date")
        .amountColumn("amount")
        .currencyColumn("currency")
        .counterpartyNameColumn("counterparty_name")
        .referenceColumn("reference")
        .attributeColumn("bank_account")
        .build();
```

Read records:

```java
List<ReconciliationRecord> records = jdbcReader.read(
        """
        select
            id,
            transaction_date,
            amount,
            currency,
            counterparty_name,
            reference,
            bank_account
        from bank_transactions
        where transaction_date between ? and ?
        """,
        mapping,
        fromDate,
        toDate
);
```

---

## 4. SQL column mapping

`JdbcQueryMapping` tells ReconcileM which SQL result column maps to which `ReconciliationRecord` field.

Required fields:

```text
id
transactionDate
amount
currency
counterpartyName
reference
```

Extra fields can be stored as attributes:

```java
.attributeColumn("bank_account")
.attributeColumn("terminal_id")
.attributeColumn("gateway_name")
```

These values will be available in:

```java
record.attributes()
```

---

## 5. Recommended SQL style

Use clear aliases in SQL.

Example:

```sql
select
    tx_id as id,
    operation_date as transaction_date,
    total_amount as amount,
    currency_code as currency,
    payer_name as counterparty_name,
    payment_reference as reference
from bank_transactions
```

Then mapping becomes simple:

```java
JdbcQueryMapping mapping = JdbcQueryMapping.builder()
        .sourceName("bank")
        .idColumn("id")
        .transactionDateColumn("transaction_date")
        .amountColumn("amount")
        .currencyColumn("currency")
        .counterpartyNameColumn("counterparty_name")
        .referenceColumn("reference")
        .build();
```

---

## 6. Spring Boot starter behavior

If a Spring Boot application has a `DataSource` bean, ReconcileM auto-configures:

```java
JdbcReconciliationRecordReader
```

So developers can inject it:

```java
@Service
public class DatabaseReconciliationService {

    private final JdbcReconciliationRecordReader jdbcReader;

    public DatabaseReconciliationService(JdbcReconciliationRecordReader jdbcReader) {
        this.jdbcReader = jdbcReader;
    }
}
```

If no `DataSource` exists, ReconcileM does not create the JDBC reader. The application still starts normally.

---

## 7. Example database reconciliation flow

```java
List<ReconciliationRecord> bankRecords = jdbcReader.read(
        "select * from bank_transactions",
        bankMapping
);

List<ReconciliationRecord> invoiceRecords = jdbcReader.read(
        "select * from invoices",
        invoiceMapping
);

ReconciliationJob job = jobFactory.defaultJob(
        "BANK_DB_TO_INVOICE_DB",
        "bank",
        "invoice-system"
);

ReconciliationResult result = reconciliationEngine.reconcile(
        bankRecords,
        invoiceRecords,
        job
);
```

---

## 8. Error handling

JDBC read errors are wrapped in:

```text
JdbcReadException
```

Common causes:

```text
missing required column
null required value
invalid amount value
unsupported date format
SQL execution failure
```

---

## 9. Current limitations

Current JDBC support only reads records.

It does not yet persist reconciliation results.

Result persistence will be added later with tables such as:

```text
reconcilem_job_run
reconcilem_match_result
reconcilem_unmatched_record
```
