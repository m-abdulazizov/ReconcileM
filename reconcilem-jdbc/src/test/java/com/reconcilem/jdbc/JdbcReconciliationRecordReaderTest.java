package com.reconcilem.jdbc;

import com.reconcilem.core.model.ReconciliationRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcReconciliationRecordReaderTest {

    private JdbcTemplate jdbcTemplate;
    private JdbcReconciliationRecordReader reader;

    @BeforeEach
    void setUp() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:reconcilem_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        jdbcTemplate = new JdbcTemplate(dataSource);
        reader = new JdbcReconciliationRecordReader(jdbcTemplate);

        jdbcTemplate.execute("drop table if exists bank_transactions");

        jdbcTemplate.execute("""
                create table bank_transactions (
                    id varchar(100) primary key,
                    transaction_date date not null,
                    amount numeric(19, 2) not null,
                    currency varchar(10),
                    counterparty_name varchar(255),
                    reference varchar(255),
                    bank_account varchar(100)
                )
                """);

        jdbcTemplate.update("""
                insert into bank_transactions (
                    id,
                    transaction_date,
                    amount,
                    currency,
                    counterparty_name,
                    reference,
                    bank_account
                ) values (?, ?, ?, ?, ?, ?, ?)
                """,
                "BANK_TX_1",
                "2026-06-01",
                new BigDecimal("1000.00"),
                "UZS",
                "ACME LLC",
                "INV-1",
                "UZ123"
        );
    }

    @Test
    void shouldReadReconciliationRecordsFromDatabase() {
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

        List<ReconciliationRecord> records = reader.read(
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
                """,
                mapping
        );

        assertThat(records).hasSize(1);

        ReconciliationRecord record = records.getFirst();

        assertThat(record.id()).isEqualTo("BANK_TX_1");
        assertThat(record.source()).isEqualTo("bank");
        assertThat(record.amount()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(record.currency()).isEqualTo("UZS");
        assertThat(record.counterpartyName()).isEqualTo("ACME LLC");
        assertThat(record.reference()).isEqualTo("INV-1");
        assertThat(record.attributes()).containsEntry("bank_account", "UZ123");
    }
}