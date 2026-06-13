package com.reconcilem.jdbc;

import com.reconcilem.core.model.ReconciliationRecord;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class JdbcReconciliationRecordReader {

    private final JdbcTemplate jdbcTemplate;

    public JdbcReconciliationRecordReader(DataSource dataSource) {
        this(new JdbcTemplate(Objects.requireNonNull(dataSource, "DataSource must not be null")));
    }

    public JdbcReconciliationRecordReader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate, "JdbcTemplate must not be null");
    }

    public List<ReconciliationRecord> read(String sql, JdbcQueryMapping mapping, Object... args) {
        Objects.requireNonNull(sql, "SQL query must not be null");
        Objects.requireNonNull(mapping, "JDBC mapping must not be null");

        if (sql.isBlank()) {
            throw new JdbcReadException("SQL query must not be blank");
        }

        try {
            return jdbcTemplate.queryForList(sql, args)
                    .stream()
                    .map(row -> mapRow(row, mapping))
                    .toList();
        } catch (Exception ex) {
            throw new JdbcReadException("Failed to read reconciliation records using SQL query", ex);
        }
    }

    private ReconciliationRecord mapRow(Map<String, Object> row, JdbcQueryMapping mapping) {
        String id = asRequiredString(row, mapping.idColumn());
        LocalDate transactionDate = asRequiredLocalDate(row, mapping.transactionDateColumn());
        BigDecimal amount = asRequiredBigDecimal(row, mapping.amountColumn());

        return new ReconciliationRecord(
                id,
                mapping.sourceName(),
                transactionDate,
                amount,
                asOptionalString(row, mapping.currencyColumn()),
                asOptionalString(row, mapping.counterpartyNameColumn()),
                asOptionalString(row, mapping.referenceColumn()),
                extractAttributes(row, mapping)
        );
    }

    private String asRequiredString(Map<String, Object> row, String column) {
        Object value = required(row, column);
        String text = String.valueOf(value).trim();

        if (text.isBlank()) {
            throw new JdbcReadException("Required JDBC value is blank for column: " + column);
        }

        return text;
    }

    private String asOptionalString(Map<String, Object> row, String column) {
        Object value = findValue(row, column);

        if (value == null) {
            return "";
        }

        return String.valueOf(value).trim();
    }

    private LocalDate asRequiredLocalDate(Map<String, Object> row, String column) {
        Object value = required(row, column);

        if (value instanceof LocalDate localDate) {
            return localDate;
        }

        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.toLocalDate();
        }

        if (value instanceof Date date) {
            return date.toLocalDate();
        }

        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime().toLocalDate();
        }

        if (value instanceof CharSequence text) {
            return LocalDate.parse(text.toString().trim());
        }

        throw new JdbcReadException("Unsupported date value for column " + column + ": " + value);
    }

    private BigDecimal asRequiredBigDecimal(Map<String, Object> row, String column) {
        Object value = required(row, column);

        if (value instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }

        if (value instanceof Number number) {
            return new BigDecimal(number.toString());
        }

        if (value instanceof CharSequence text) {
            String normalized = text.toString()
                    .replace(" ", "")
                    .replace(",", "")
                    .trim();

            return new BigDecimal(normalized);
        }

        throw new JdbcReadException("Unsupported amount value for column " + column + ": " + value);
    }

    private Object required(Map<String, Object> row, String column) {
        Object value = findValue(row, column);

        if (value == null) {
            throw new JdbcReadException("Required JDBC column not found or value is null: " + column);
        }

        return value;
    }

    private Object findValue(Map<String, Object> row, String column) {
        if (row.containsKey(column)) {
            return row.get(column);
        }

        for (Map.Entry<String, Object> entry : row.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(column)) {
                return entry.getValue();
            }
        }

        return null;
    }

    private Map<String, Object> extractAttributes(Map<String, Object> row, JdbcQueryMapping mapping) {
        Map<String, Object> attributes = new LinkedHashMap<>();

        for (String column : mapping.attributeColumns()) {
            Object value = findValue(row, column);

            if (value != null) {
                attributes.put(column, value);
            }
        }

        return attributes;
    }
}