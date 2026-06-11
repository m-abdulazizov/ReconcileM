package com.reconcilem.core.normalizer;

import com.reconcilem.core.model.ReconciliationRecord;

public class ReferenceNormalizer implements RecordNormalizer
{
    @Override
    public ReconciliationRecord normalize(ReconciliationRecord record) {
        String normalizedReference = normalizeReference(record.reference());

        return new ReconciliationRecord(
                record.id(),
                record.source(),
                record.transactionDate(),
                record.amount(),
                record.currency(),
                record.counterpartyName(),
                normalizedReference,
                record.attributes()
        );
    }

    private String normalizeReference(String value) {
        if (value == null) {
            return "";
        }

        return value.trim()
                .toUpperCase()
                .replace("№", "NO")
                .replaceAll("[^A-Z0-9]", "");
    }
}