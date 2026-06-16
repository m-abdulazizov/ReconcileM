package com.reconcilem.jdbc;

import com.reconcilem.core.model.MatchDecision;
import com.reconcilem.core.model.MatchResult;
import com.reconcilem.core.model.MatchScore;
import com.reconcilem.core.model.ReconciliationRecord;
import com.reconcilem.core.model.ReconciliationResult;
import com.reconcilem.core.model.ReconciliationSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcReconciliationResultRepositoryTest {

    private JdbcTemplate jdbcTemplate;
    private JdbcReconciliationResultRepository repository;

    @BeforeEach
    void setUp() {
        DataSource dataSource = dataSource();

        jdbcTemplate = new JdbcTemplate(dataSource);
        repository = new JdbcReconciliationResultRepository(jdbcTemplate);

        jdbcTemplate.execute("drop table if exists reconcilem_unmatched_record");
        jdbcTemplate.execute("drop table if exists reconcilem_match_result");
        jdbcTemplate.execute("drop table if exists reconcilem_job_run");

        repository.createSchema();
    }

    @Test
    void shouldPersistReconciliationResult() {
        ReconciliationRecord bankRecord = record("BANK_TX_1", "bank", "ACME LLC", "INV-1");
        ReconciliationRecord invoiceRecord = record("INV_1", "invoice-system", "ACME LIMITED", "INV-1");
        ReconciliationRecord unmatchedRecord = record("BANK_TX_2", "bank", "BRAVO LLC", "INV-2");

        ReconciliationResult result = new ReconciliationResult(
                List.of(new MatchResult(
                        bankRecord,
                        invoiceRecord,
                        90,
                        MatchDecision.MATCHED,
                        List.of(
                                new MatchScore("AMOUNT_EXACT_MATCH", 40, "Amounts are equal"),
                                new MatchScore("REFERENCE_EXACT_MATCH", 25, "References are equal")
                        )
                )),
                List.of(),
                List.of(),
                List.of(),
                List.of(unmatchedRecord),
                List.of(),
                new ReconciliationSummary(2, 1, 1, 0, 1, 0)
        );

        String runId = repository.save("BANK_TO_INVOICE", result);

        Integer runCount = jdbcTemplate.queryForObject(
                "select count(*) from reconcilem_job_run where run_id = ?",
                Integer.class,
                runId
        );
        Integer matchCount = jdbcTemplate.queryForObject(
                "select count(*) from reconcilem_match_result where run_id = ?",
                Integer.class,
                runId
        );
        Integer unmatchedCount = jdbcTemplate.queryForObject(
                "select count(*) from reconcilem_unmatched_record where run_id = ?",
                Integer.class,
                runId
        );

        assertThat(runId).isNotBlank();
        assertThat(runCount).isEqualTo(1);
        assertThat(matchCount).isEqualTo(1);
        assertThat(unmatchedCount).isEqualTo(1);

        Map<String, Object> savedMatch = jdbcTemplate.queryForMap(
                "select source_record_id, target_record_id, decision, total_score, score_details from reconcilem_match_result where run_id = ?",
                runId
        );

        assertThat(savedMatch)
                .containsEntry("SOURCE_RECORD_ID", "BANK_TX_1")
                .containsEntry("TARGET_RECORD_ID", "INV_1")
                .containsEntry("DECISION", "MATCHED");
        assertThat(savedMatch.get("TOTAL_SCORE")).isEqualTo(90);
        assertThat(savedMatch.get("SCORE_DETAILS").toString()).contains("AMOUNT_EXACT_MATCH=40");
    }

    private ReconciliationRecord record(
            String id,
            String source,
            String counterpartyName,
            String reference
    ) {
        return new ReconciliationRecord(
                id,
                source,
                LocalDate.of(2026, 6, 1),
                new BigDecimal("1000.00"),
                "UZS",
                counterpartyName,
                reference,
                Map.of("business_unit", "payments")
        );
    }

    private DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:reconcilem_result_repository_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }
}
