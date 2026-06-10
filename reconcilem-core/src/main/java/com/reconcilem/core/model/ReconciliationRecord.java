package com.reconcilem.core.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

public record ReconciliationRecord(
        String id,
        String source,
        LocalDate transactionDate,
        BigDecimal amount,
        String currency,
        String counterpartyName,
        String reference,
        Map<String, Object> attributes
)
{
    public ReconciliationRecord{
    Objects.requireNonNull(id, "Record id must not be null");
    Objects.requireNonNull(source, "Record source must not be null");
    Objects.requireNonNull(amount, "Record amount must not be null");

    currency = currency == null ? "" : currency.trim().toUpperCase();
    counterpartyName = counterpartyName == null ? "" : counterpartyName.trim();
    reference = reference == null ? "" : reference.trim();
    attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
