package com.reconcilem.csv;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.reconcilem.core.model.ReconciliationRecord;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CsvReconciliationRecordReader {

    private final CsvMapper csvMapper = new CsvMapper();

    public List<ReconciliationRecord> read(Path path, CsvMapping mapping) {
        Objects.requireNonNull(path, "CSV path must not be null");

        try (InputStream inputStream = Files.newInputStream(path)) {
            return read(inputStream, mapping);
        } catch (IOException ex) {
            throw new CsvReadException("Failed to read CSV file: " + path, ex);
        }
    }

    public List<ReconciliationRecord> read(InputStream inputStream, CsvMapping mapping) {
        Objects.requireNonNull(inputStream, "CSV input stream must not be null");
        Objects.requireNonNull(mapping, "CSV mapping must not be null");

        CsvSchema schema = CsvSchema.emptySchema().withHeader();

        try {
            MappingIterator<Map<String, String>> iterator = csvMapper
                    .readerFor(new TypeReference<Map<String, String>>() {
                    })
                    .with(schema)
                    .readValues(inputStream);

            return iterator.readAll()
                    .stream()
                    .map(row -> mapRow(row, mapping))
                    .toList();
        } catch (IOException ex) {
            throw new CsvReadException("Failed to parse CSV content", ex);
        }
    }

    private ReconciliationRecord mapRow(Map<String, String> row, CsvMapping mapping) {
        String id = required(row, mapping.idColumn());
        LocalDate transactionDate = LocalDate.parse(
                required(row, mapping.transactionDateColumn()),
                mapping.dateFormatter()
        );
        BigDecimal amount = parseAmount(required(row, mapping.amountColumn()));

        return new ReconciliationRecord(
                id,
                mapping.sourceName(),
                transactionDate,
                amount,
                optional(row, mapping.currencyColumn()),
                optional(row, mapping.counterpartyNameColumn()),
                optional(row, mapping.referenceColumn()),
                extractAttributes(row, mapping)
        );
    }

    private String required(Map<String, String> row, String column) {
        if (!row.containsKey(column)) {
            throw new CsvReadException("Required CSV column not found: " + column);
        }

        String value = row.get(column);

        if (value == null || value.isBlank()) {
            throw new CsvReadException("Required CSV value is blank for column: " + column);
        }

        return value.trim();
    }

    private String optional(Map<String, String> row, String column) {
        if (!row.containsKey(column)) {
            return "";
        }

        String value = row.get(column);
        return value == null ? "" : value.trim();
    }

    private BigDecimal parseAmount(String value) {
        try {
            String normalized = value
                    .replace(" ", "")
                    .replace(",", "");

            return new BigDecimal(normalized);
        } catch (NumberFormatException ex) {
            throw new CsvReadException("Invalid amount value: " + value, ex);
        }
    }

    private Map<String, Object> extractAttributes(Map<String, String> row, CsvMapping mapping) {
        Map<String, Object> attributes = new LinkedHashMap<>();

        for (String column : mapping.attributeColumns()) {
            if (row.containsKey(column)) {
                attributes.put(column, row.get(column));
            }
        }

        return attributes;
    }
}