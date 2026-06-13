package com.reconcilem.jdbc;

import java.util.LinkedHashSet;
import java.util.Set;

public final class JdbcQueryMapping {

    private final String sourceName;
    private final String idColumn;
    private final String transactionDateColumn;
    private final String amountColumn;
    private final String currencyColumn;
    private final String counterpartyNameColumn;
    private final String referenceColumn;
    private final Set<String> attributeColumns;

    private JdbcQueryMapping(Builder builder) {
        this.sourceName = requireText(builder.sourceName, "Source name");
        this.idColumn = requireText(builder.idColumn, "ID column");
        this.transactionDateColumn = requireText(builder.transactionDateColumn, "Transaction date column");
        this.amountColumn = requireText(builder.amountColumn, "Amount column");
        this.currencyColumn = requireText(builder.currencyColumn, "Currency column");
        this.counterpartyNameColumn = requireText(builder.counterpartyNameColumn, "Counterparty name column");
        this.referenceColumn = requireText(builder.referenceColumn, "Reference column");
        this.attributeColumns = Set.copyOf(builder.attributeColumns);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String sourceName() {
        return sourceName;
    }

    public String idColumn() {
        return idColumn;
    }

    public String transactionDateColumn() {
        return transactionDateColumn;
    }

    public String amountColumn() {
        return amountColumn;
    }

    public String currencyColumn() {
        return currencyColumn;
    }

    public String counterpartyNameColumn() {
        return counterpartyNameColumn;
    }

    public String referenceColumn() {
        return referenceColumn;
    }

    public Set<String> attributeColumns() {
        return attributeColumns;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }

        return value.trim();
    }

    public static final class Builder {

        private String sourceName;
        private String idColumn;
        private String transactionDateColumn;
        private String amountColumn;
        private String currencyColumn;
        private String counterpartyNameColumn;
        private String referenceColumn;
        private final Set<String> attributeColumns = new LinkedHashSet<>();

        private Builder() {
        }

        public Builder sourceName(String sourceName) {
            this.sourceName = sourceName;
            return this;
        }

        public Builder idColumn(String idColumn) {
            this.idColumn = idColumn;
            return this;
        }

        public Builder transactionDateColumn(String transactionDateColumn) {
            this.transactionDateColumn = transactionDateColumn;
            return this;
        }

        public Builder amountColumn(String amountColumn) {
            this.amountColumn = amountColumn;
            return this;
        }

        public Builder currencyColumn(String currencyColumn) {
            this.currencyColumn = currencyColumn;
            return this;
        }

        public Builder counterpartyNameColumn(String counterpartyNameColumn) {
            this.counterpartyNameColumn = counterpartyNameColumn;
            return this;
        }

        public Builder referenceColumn(String referenceColumn) {
            this.referenceColumn = referenceColumn;
            return this;
        }

        public Builder attributeColumn(String attributeColumn) {
            if (attributeColumn != null && !attributeColumn.isBlank()) {
                this.attributeColumns.add(attributeColumn.trim());
            }

            return this;
        }

        public Builder attributeColumns(Set<String> attributeColumns) {
            if (attributeColumns != null) {
                attributeColumns.forEach(this::attributeColumn);
            }

            return this;
        }

        public JdbcQueryMapping build() {
            return new JdbcQueryMapping(this);
        }
    }
}