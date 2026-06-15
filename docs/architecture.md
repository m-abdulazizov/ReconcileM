# ReconcileM Architecture

ReconcileM is organized as a reusable Java financial reconciliation framework. The project is split into small modules so the core matching engine can stay independent from Spring Boot, CSV parsing, and database infrastructure.

## Design Goals

- Keep reconciliation logic reusable across plain Java, Spring Boot, batch jobs, and future services.
- Make every decision explainable through rule-level scores.
- Allow applications to plug in their own readers, rules, normalizers, thresholds, and exporters.
- Keep adapters such as CSV and JDBC outside the core domain module.

## Modules

| Module | Responsibility |
|---|---|
| `reconcilem-core` | Domain model, matching rules, normalizers, scoring, and reconciliation engine |
| `reconcilem-csv` | CSV input mapping and CSV result export |
| `reconcilem-jdbc` | JDBC record reading and reconciliation result persistence |
| `reconcilem-spring-boot-starter` | Spring Boot auto-configuration and default framework beans |
| `reconcilem-example-app` | Runnable demo application that shows CSV reconciliation |
| `reconcilem-test` | Reserved for future testing utilities |

## Core Flow

```text
source records
    |
    v
normalizers
    |
    v
matching rules ------------+
    |                      |
    v                      |
rule scores and reasons    |
    |                      |
    +----------------------+
    |
    v
total score
    |
    v
decision: MATCHED / POSSIBLE_MATCH / UNMATCHED / DUPLICATE / CONFLICT
    |
    v
ReconciliationResult
```

The engine compares each source record against target records, evaluates all configured rules, calculates a total score, and classifies the best candidate using configured thresholds.

## Core Domain Objects

- `ReconciliationRecord`: a generic financial record from a source system.
- `ReconciliationJob`: describes what is being reconciled and which rules/thresholds apply.
- `MatchingRule`: compares one source record with one target record.
- `MatchScore`: rule-level score and explanation.
- `MatchResult`: result for one source/target comparison.
- `ReconciliationResult`: final grouped output.
- `ReconciliationSummary`: aggregate counts for reporting.

## Extension Points

Applications can extend the framework by providing:

- custom `MatchingRule` implementations
- custom `RecordNormalizer` implementations
- custom thresholds per `ReconciliationJob`
- custom readers/exporters outside the core module
- replacement Spring beans when using the starter

## Adapter Modules

### CSV

The CSV module maps arbitrary CSV column names into `ReconciliationRecord` fields using `CsvMapping`. This avoids hardcoding bank, invoice, or payment-provider file formats.

### JDBC

The JDBC module maps SQL query results into `ReconciliationRecord` fields using `JdbcQueryMapping`. It can also persist reconciliation runs into default result tables:

```text
reconcilem_job_run
reconcilem_match_result
reconcilem_unmatched_record
```

### Spring Boot

The starter creates default beans for the engine, CSV reader/writer, job factory, and JDBC components when the required infrastructure is available. Users can override these beans with their own implementations.

## Current Limitations

- Matching is intentionally simple and in-memory for the first versions.
- JDBC persistence uses a generic default schema.
- The framework does not yet include one-to-many or many-to-one matching.
- `reconcilem-test` is reserved but not implemented yet.
- Public API stability and publishing configuration are still future release work.
