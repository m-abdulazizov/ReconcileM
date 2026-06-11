package com.reconcilem.core.normalizer;

import com.reconcilem.core.model.ReconciliationRecord;

public class CurrencyNormalizer implements RecordNormalizer
{
    @Override
    public ReconciliationRecord normalize(ReconciliationRecord record)
    {
        String normalizedCurrency = record.currency() == null
                ? ""
                : record.currency().trim().toUpperCase();
        return new ReconciliationRecord(
                record.id(),
                record.source(),
                record.transactionDate(),
                record.amount(),
                record.currency(),
                record.counterpartyName(),
                record.reference(),
                record.attributes()
        );
    }
}