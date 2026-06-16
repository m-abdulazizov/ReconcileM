# ReconcileM

ReconcileM is a Java financial reconciliation framework for matching records across banks, invoices, payment gateways, refunds, accounting systems, and internal business systems.

It is not a finance application. It provides reusable reconciliation infrastructure: record normalization, rule-based matching, confidence scoring, explainable decisions, CSV/JDBC adapters, Spring Boot integration, and result export/persistence.

## Why It Exists

Financial data often describes the same business event in different ways:

```text
Bank:    BANK_TX_1001 | 2026-06-01 | ACME LLC     | 999000.00  | UZS | Payment for INV-889
Invoice: INV_889      | 2026-05-30 | ACME LIMITED | 1000000.00 | UZS | INV-889
```

Humans can often see that these records probably match. ReconcileM turns that judgment into configurable, testable, explainable framework logic.

## Features

- Pure Java core with no Spring dependency
- Rule-based matching with confidence scores
- Explainable match reasons per rule
- Configurable thresholds for `MATCHED` and `POSSIBLE_MATCH`
- Normalization for references, currencies, and counterparty names
- Duplicate and conflict detection
- CSV record reading and CSV report export
- JDBC record reading and result persistence
- Spring Boot auto-configuration
- Testing helpers for applications that use the framework

## Modules

ReconcileM currently contains:

| Module | Purpose |
|---|---|
| `reconcilem-core` | Core reconciliation engine, records, rules, normalization |
| `reconcilem-csv` | CSV record reader and CSV result exporter |
| `reconcilem-jdbc` | JDBC/database record reader and result persistence |
| `reconcilem-spring-boot-starter` | Spring Boot auto-configuration |
| `reconcilem-example-app` | Demo Spring Boot application |
| `reconcilem-test` | Test fixtures and assertions for framework users |

## Documentation

- [Architecture](docs/architecture.md)
- [Getting Started](docs/getting-started.md)
- [Matching Rules](docs/matching-rules.md)
- [CSV Module](docs/csv-module.md)
- [JDBC Support](docs/jdbc-support.md)
- [Spring Boot Starter](docs/spring-boot-starter.md)
- [Extension Points](docs/extension-points.md)

## Quick Start

Create a reconciliation job:

```java
ReconciliationJob job = jobFactory.defaultJob(
        "BANK_TO_INVOICE",
        "bank",
        "invoice-system"
);
```

Run reconciliation:

```java
ReconciliationResult result = reconciliationEngine.reconcile(
        bankRecords,
        invoiceRecords,
        job
);
```

Inspect output:

```java
result.matched();
result.possibleMatches();
result.unmatchedSourceRecords();
result.summary();
```

See [Getting Started](docs/getting-started.md) for the full Spring Boot and CSV walkthrough.

## Example App

Run the demo app:

```powershell
.\gradlew.bat :reconcilem-example-app:bootRun
```

Open:

```text
http://localhost:8081/demo/reconcile-csv
```

Export CSV report:

```text
http://localhost:8081/demo/reconcile-csv/export
```

Run the JDBC demo:

```text
http://localhost:8081/demo/reconcile-jdbc
```

Persist the JDBC reconciliation result:

```text
http://localhost:8081/demo/reconcile-jdbc/persist
```

## Technical Highlights

This project demonstrates:

- multi-module Gradle project structure
- framework-style API design
- domain modeling for financial reconciliation
- adapter separation between core, CSV, JDBC, and Spring Boot
- Spring Boot auto-configuration
- database integration with H2-backed tests
- practical documentation and extension points

## Current Status

ReconcileM is in active development as a portfolio-quality framework project. The core, CSV, JDBC, Spring Boot starter, example app, and test utilities are implemented. Future work includes publishing setup, CI, broader integration tests, and advanced matching modes such as one-to-many reconciliation.

## License

This project is licensed under the [MIT License](LICENSE).
