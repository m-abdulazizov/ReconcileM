package com.reconcilem.test;

import com.reconcilem.core.model.ReconciliationRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RecordFixtures {

    private RecordFixtures() {
    }

    public static ReconciliationRecord bankRecord(String id, BigDecimal amount, String reference) {
        return record(id, "bank", amount, "ACME LLC", reference);
    }

    public static ReconciliationRecord invoiceRecord(String id, BigDecimal amount, String reference) {
        return record(id, "invoice-system", amount, "ACME LIMITED", reference);
    }

    public static ReconciliationRecord record(
            String id,
            String source,
            BigDecimal amount,
            String counterpartyName,
            String reference
    ) {
        return builder()
                .id(id)
                .source(source)
                .amount(amount)
                .counterpartyName(counterpartyName)
                .reference(reference)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String id = "RECORD_1";
        private String source = "source-system";
        private LocalDate transactionDate = LocalDate.of(2026, 6, 1);
        private BigDecimal amount = new BigDecimal("1000.00");
        private String currency = "UZS";
        private String counterpartyName = "ACME LLC";
        private String reference = "INV-1";
        private final Map<String, Object> attributes = new LinkedHashMap<>();

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Builder transactionDate(LocalDate transactionDate) {
            this.transactionDate = transactionDate;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder counterpartyName(String counterpartyName) {
            this.counterpartyName = counterpartyName;
            return this;
        }

        public Builder reference(String reference) {
            this.reference = reference;
            return this;
        }

        public Builder attribute(String name, Object value) {
            this.attributes.put(name, value);
            return this;
        }

        public ReconciliationRecord build() {
            return new ReconciliationRecord(
                    id,
                    source,
                    transactionDate,
                    amount,
                    currency,
                    counterpartyName,
                    reference,
                    attributes
            );
        }
    }
}
