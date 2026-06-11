package com.reconcilem.core.normalizer;

import com.reconcilem.core.model.ReconciliationRecord;

import java.util.List;

public class DefaultRecordNormalizer implements RecordNormalizer {
    private final CompositeRecordNormalizer delegate = new CompositeRecordNormalizer(
            List.of(
                    new CurrencyNormalizer(),
                    new ReferenceNormalizer(),
                    new CounterpartyNameNormalizer()
            )
    );

    @Override
    public ReconciliationRecord normalize(ReconciliationRecord record) {
        return delegate.normalize(record);
    }
}
