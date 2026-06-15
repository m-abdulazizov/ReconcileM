# ReconcileM

ReconcileM is a Java financial reconciliation framework for matching records across banks, invoices, payment gateways, refunds, accounting systems, and internal business systems.

It is not a finance application. It provides reusable reconciliation infrastructure: record normalization, rule-based matching, confidence scoring, explainable decisions, CSV/JDBC adapters, Spring Boot integration, and result export/persistence.

## Modules

ReconcileM currently contains:

| Module | Purpose |
|---|---|
| `reconcilem-core` | Core reconciliation engine, records, rules, normalization |
| `reconcilem-csv` | CSV record reader and CSV result exporter |
| `reconcilem-jdbc` | JDBC/database record reader and result persistence |
| `reconcilem-spring-boot-starter` | Spring Boot auto-configuration |
| `reconcilem-example-app` | Demo Spring Boot application |

## Documentation

- [Architecture](docs/architecture.md)
- [Getting Started](docs/getting-started.md)
- [JDBC Support](docs/jdbc-support.md)

## Quick Start

See [Getting Started](docs/getting-started.md) for usage with Spring Boot, CSV reading, reconciliation jobs, and CSV report export.
